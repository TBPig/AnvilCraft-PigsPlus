package dev.anvilcraft.pigsplus.wireless;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.GridAdapterItem;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public final class GridAdapterManager {
    private static final Map<ResourceKey<Level>, Map<BlockPos, GridAdapterComponent>> ACTIVE = new HashMap<>();

    private GridAdapterManager() {
    }

    public static void toggle(ServerLevel level, BlockPos pos, Direction side, int mode, int power, Player player) {
        power = Math.clamp(power, 1, GridAdapterItem.getMaxPower());
        IEnergyStorage storage = GridAdapterComponent.getEnergyStorage(level, pos, side);
        if (storage == null) {
            player.displayClientMessage(
                Component.translatable("message.anvilcraft_pigsplus.grid_adapter.no_energy_storage"),
                true
            );
            return;
        }
        if (mode == GridAdapterItem.INPUT_MODE && !storage.canReceive()) {
            player.displayClientMessage(
                Component.translatable("message.anvilcraft_pigsplus.grid_adapter.no_receive"),
                true
            );
            return;
        }
        if (mode == GridAdapterItem.OUTPUT_MODE && !storage.canExtract()) {
            player.displayClientMessage(
                Component.translatable("message.anvilcraft_pigsplus.grid_adapter.no_extract"),
                true
            );
            return;
        }

        GridAdapterData data = GridAdapterData.get(level);
        GridAdapterData.Entry existing = data.get(pos);
        if (existing == null) {
            data.set(pos, new GridAdapterData.Entry(side, mode, power));
            addActive(level, pos, createComponent(level, pos, side, mode, power));
            player.displayClientMessage(
                Component.translatable(
                    "message.anvilcraft_pigsplus.grid_adapter.enabled",
                    modeName(mode)
                ),
                true
            );
            return;
        }

        if (existing.mode() == mode) {
            if (existing.power() == power) {
                removeActive(level, pos);
                data.remove(pos);
                player.displayClientMessage(
                    Component.translatable("message.anvilcraft_pigsplus.grid_adapter.disabled"),
                    true
                );
            } else {
                removeActive(level, pos);
                data.set(pos, new GridAdapterData.Entry(side, mode, power));
                addActive(level, pos, createComponent(level, pos, side, mode, power));
                player.displayClientMessage(
                    Component.translatable("message.anvilcraft_pigsplus.grid_adapter.updated"),
                    true
                );
            }
        } else {
            removeActive(level, pos);
            data.set(pos, new GridAdapterData.Entry(side, mode, power));
            addActive(level, pos, createComponent(level, pos, side, mode, power));
            player.displayClientMessage(
                Component.translatable(
                    "message.anvilcraft_pigsplus.grid_adapter.switched",
                    modeName(mode)
                ),
                true
            );
        }

    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        for (ServerLevel level : server.getAllLevels()) {
            syncLevel(level);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            unloadLevel(serverLevel);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ACTIVE.clear();
    }

    private static void syncLevel(ServerLevel level) {
        // 服务端重启或维度重新加载后，从 SavedData 恢复虚拟电网元件。
        GridAdapterData data = GridAdapterData.get(level);
        Map<BlockPos, GridAdapterComponent> active =
            ACTIVE.computeIfAbsent(level.dimension(), ignored -> new HashMap<>());
        for (Map.Entry<BlockPos, GridAdapterData.Entry> entry : data.snapshot().entrySet()) {
            BlockPos pos = entry.getKey();
            GridAdapterData.Entry binding = entry.getValue();
            if (level.isLoaded(pos)
                && GridAdapterComponent.getEnergyStorage(level, pos, binding.side()) == null) {
                removeActive(level, pos);
                data.remove(pos);
                continue;
            }
            int power = Math.clamp(binding.power(), 1, GridAdapterItem.getMaxPower());
            if (power != binding.power()) {
                binding = new GridAdapterData.Entry(binding.side(), binding.mode(), power);
                data.set(pos, binding);
            }
            GridAdapterComponent component = active.get(pos);
            if (component == null) {
                component = createComponent(level, pos, binding.side(), binding.mode(), power);
                active.put(pos, component);
                PowerGrid.addComponent(component);
            } else if (component.getSide() != binding.side() || component.getMode() != binding.mode()) {
                PowerGrid.removeComponent(component);
                component = createComponent(level, pos, binding.side(), binding.mode(), power);
                active.put(pos, component);
                PowerGrid.addComponent(component);
            } else {
                if (component.getPower() != power) {
                    component.setPower(power);
                }
            }
        }
    }

    private static void addActive(ServerLevel level, BlockPos pos, GridAdapterComponent component) {
        ACTIVE.computeIfAbsent(level.dimension(), ignored -> new HashMap<>()).put(pos.immutable(), component);
        PowerGrid.addComponent(component);
    }

    private static void removeActive(ServerLevel level, BlockPos pos) {
        Map<BlockPos, GridAdapterComponent> active = ACTIVE.get(level.dimension());
        if (active == null) return;
        GridAdapterComponent component = active.remove(pos);
        if (component != null) {
            PowerGrid.removeComponent(component);
        }
    }

    private static void unloadLevel(ServerLevel level) {
        Map<BlockPos, GridAdapterComponent> active = ACTIVE.remove(level.dimension());
        if (active == null) return;
        for (GridAdapterComponent component : active.values()) {
            PowerGrid.removeComponent(component);
        }
    }

    private static Component modeName(int mode) {
        String key = mode == GridAdapterItem.OUTPUT_MODE
                     ? "screen.anvilcraft_pigsplus.grid_adapter.output"
                     : "screen.anvilcraft_pigsplus.grid_adapter.input";
        return Component.translatable(key);
    }

    private static GridAdapterComponent createComponent(
        ServerLevel level,
        BlockPos pos,
        Direction side,
        int mode,
        int power
    ) {
        return mode == GridAdapterItem.OUTPUT_MODE
               ? new GridAdapterOutputComponent(level, pos, side, power)
               : new GridAdapterInputComponent(level, pos, side, power);
    }
}
