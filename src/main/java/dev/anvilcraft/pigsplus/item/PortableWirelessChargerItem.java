package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonDataComponents;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.integration.curios.CuriosCompat;
import dev.anvilcraft.pigsplus.inventory.PortableWirelessChargerMenu;
import dev.anvilcraft.pigsplus.network.PortableWirelessChargerInitPacket;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.DynamicPowerComponent;
import dev.dubhe.anvilcraft.api.power.IDynamicPowerComponentHolder;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 便携无线充电器：每把物品独立保存转换量，并在玩家进入电网后为背包中的 FE 物品充能。
 */
@SuppressWarnings("unused")
public class PortableWirelessChargerItem extends Item {
    public static final int DEFAULT_POWER = 16;
    // 每个玩家只缓存一个聚合 PowerConsumption，并随玩家生命周期显式清理
    private static final Map<ServerPlayer, PortableWirelessChargerState> PLAYER_POWER_STATES =
        Collections.synchronizedMap(new WeakHashMap<>());

    public PortableWirelessChargerItem(Properties properties) {
        super(properties);
    }

    /**
     * 玩家每 tick 刷新电网消耗，并在电网已确认未过载时为物品充能。
     */
    public static void playerTick(ServerPlayer player) {
        List<ItemStack> chargers = getPortableWirelessChargers(player);
        refreshPower(player, chargers);
        chargePlayerItems(player, chargers);
    }

    /**
     * 获取服务端配置允许的最大转换量，最低为 1kW。
     */
    public static int getMaxPower() {
        return Math.max(1, AnvilCraftPigsPlus.CONFIG.portableWirelessChargerMaxPowerConversion);
    }

    /**
     * 读取物品栈上的转换量，并将越界值限制在合法范围内。
     */
    public static int getPower(ItemStack stack) {
        return Math.clamp(
            stack.getOrDefault(AddonDataComponents.PORTABLE_WIRELESS_CHARGER_POWER, DEFAULT_POWER),
            1,
            getMaxPower()
        );
    }

    /**
     * 将转换量写入物品栈，并限制在合法范围内。
     */
    public static void setPower(ItemStack stack, int power) {
        stack.set(
            AddonDataComponents.PORTABLE_WIRELESS_CHARGER_POWER,
            Math.clamp(power, 1, getMaxPower())
        );
    }

    /**
     * 按玩家当前充电器重新同步电网的聚合消耗。
     */
    public static void refreshPower(ServerPlayer player) {
        refreshPower(player, getPortableWirelessChargers(player));
    }

    /**
     * 汇总玩家所有充电器的 kW，并仅在聚合值变化时替换电网中的 PowerConsumption。
     */
    private static void refreshPower(ServerPlayer player, List<ItemStack> chargers) {
        IDynamicPowerComponentHolder holder = IDynamicPowerComponentHolder.of(player);
        DynamicPowerComponent powerComponent = holder.anvilcraft$getPowerComponent();
        PowerGrid grid = powerComponent.getPowerGrid();

        long totalPower = 0;
        for (ItemStack stack : chargers) {
            totalPower += getPower(stack);
        }
        int amount = (int) Math.min(Integer.MAX_VALUE, totalPower);

        if (amount <= 0) {
            PortableWirelessChargerState existing = PLAYER_POWER_STATES.get(player);
            if (existing != null) {
                if (existing.consumption != null) {
                    powerComponent.getPowerConsumptions().remove(existing.consumption);
                }
                PLAYER_POWER_STATES.remove(player);
            }
            return;
        }

        PortableWirelessChargerState state = getState(player);
        if (state.grid != grid) {
            state.grid = grid;
            state.working = false;
        }

        if (state.consumption != null && state.consumption.amount() == amount) {
            return;
        }
        if (state.consumption != null) {
            powerComponent.getPowerConsumptions().remove(state.consumption);
        }

        state.consumption = new DynamicPowerComponent.PowerConsumption(amount);
        powerComponent.getPowerConsumptions().add(state.consumption);
        state.working = false;
        if (grid != null) {
            grid.markChanged();
        }
    }

    /**
     * 电网刻回调：电网完成本次刷新后，根据是否未过载更新玩家的可充电状态。
     */
    public static void onGridTick(ServerPlayer player, @Nullable PowerGrid grid) {
        PortableWirelessChargerState state = PLAYER_POWER_STATES.get(player);
        if (state == null) return;
        if (grid == null) {
            state.grid = null;
            state.working = false;
            return;
        }
        if (state.grid != grid) {
            state.grid = grid;
            state.working = false;
            return;
        }
        state.working = grid.isWorking();
    }

    /**
     * 按玩家当前充电器计算 FE，并为背包中的可充能物品充电。
     */
    public static void chargePlayerItems(ServerPlayer player) {
        chargePlayerItems(player, getPortableWirelessChargers(player));
    }

    /**
     * 仅在电网刚确认未过载后，向有 FE 接收能力的物品充能。
     */
    private static void chargePlayerItems(ServerPlayer player, List<ItemStack> chargers) {
        IDynamicPowerComponentHolder holder = IDynamicPowerComponentHolder.of(player);
        DynamicPowerComponent powerComponent = holder.anvilcraft$getPowerComponent();
        PortableWirelessChargerState state = PLAYER_POWER_STATES.get(player);
        if (state == null || !state.working) return;
        PowerGrid powerGrid = powerComponent.getPowerGrid();
        if (powerGrid == null) return;
        if (!powerGrid.isWorking()) return;

        if (chargers.isEmpty()) return;

        long feEnergy = 0;
        for (ItemStack stack : chargers) {
            feEnergy = Math.min(
                Integer.MAX_VALUE,
                feEnergy + (long) getPower(stack) * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency
            );
        }
        // 遍历玩家物品栏，尝试为有能量槽的物品充电
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack.isEmpty()) continue;

            IEnergyStorage itemEnergy = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy == null) continue;
            if (!itemEnergy.canReceive()) continue;

            int receiveEnergy = itemEnergy.receiveEnergy((int) feEnergy, false);
            feEnergy -= receiveEnergy;
            if (feEnergy <= 0) break;
        }
    }

    /**
     * 判断玩家背包或 Curios 中是否存在便携无线充电器。
     */
    public static boolean hasPortableWirelessCharger(ServerPlayer player) {
        return !getPortableWirelessChargers(player).isEmpty();
    }

    /**
     * 收集玩家背包与 Curios 中的实际充电器物品栈。
     */
    private static List<ItemStack> getPortableWirelessChargers(ServerPlayer player) {
        List<ItemStack> chargers = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(AddonItems.PORTABLE_WIRELESS_CHARGER.get())) {
                chargers.add(stack);
            }
        }
        chargers.addAll(CuriosCompat.getPortableWirelessChargers(player));
        return chargers;
    }

    /**
     * 获取或创建玩家对应的电网状态。
     */
    private static PortableWirelessChargerState getState(ServerPlayer player) {
        return PLAYER_POWER_STATES.computeIfAbsent(player, k -> new PortableWirelessChargerState());
    }

    /**
     * 玩家退出服务器或穿越维度时，注销聚合消耗并移除玩家状态。
     */
    public static void clearPlayerState(ServerPlayer player) {
        PortableWirelessChargerState state = PLAYER_POWER_STATES.remove(player);
        if (state == null) return;
        if (state.consumption != null) {
            IDynamicPowerComponentHolder.of(player)
                .anvilcraft$getPowerComponent()
                .getPowerConsumptions()
                .remove(state.consumption);
        }
    }

    /**
     * 服务器停止时清空所有玩家状态。
     */
    public static void clearAll() {
        PLAYER_POWER_STATES.clear();
    }

    /**
     * 主手右键打开转换量配置界面。
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        openConfigScreen(level, player);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    /**
     * 在服务端打开配置菜单，并向客户端发送当前值与上限。
     */
    private static void openConfigScreen(Level level, Player player) {
        if (level.isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof PortableWirelessChargerItem)) return;

        AddonMenuTypes.open(
            serverPlayer,
            new SimpleMenuProvider(
                (id, inventory, ignored) -> new PortableWirelessChargerMenu(id),
                Component.translatable("screen.anvilcraft_pigsplus.portable_wireless_charger.title")
            )
        );
        PacketDistributor.sendToPlayer(
            serverPlayer,
            new PortableWirelessChargerInitPacket(getPower(stack), getMaxPower())
        );
    }

    /**
     * 显示当前转换量与右键操作提示。
     */
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        int generated = (int) Math.min(
            Integer.MAX_VALUE,
            (long) getPower(stack) * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency
        );
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.portable_wireless_charger",
            getPower(stack),
            generated
        ).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.portable_wireless_charger.configure"
        ).withStyle(ChatFormatting.GRAY));
    }

    private static final class PortableWirelessChargerState {
        private @Nullable PowerGrid grid;
        private @Nullable DynamicPowerComponent.PowerConsumption consumption;
        private boolean working;
    }
}
