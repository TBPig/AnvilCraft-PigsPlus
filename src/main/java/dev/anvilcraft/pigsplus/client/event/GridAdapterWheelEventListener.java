package dev.anvilcraft.pigsplus.client.event;

import dev.anvilcraft.lib.v2.wheel.api.WheelMenuBuilder;
import dev.anvilcraft.lib.v2.wheel.api.WheelMenuModel;
import dev.anvilcraft.lib.v2.wheel.client.input.WheelScreenController;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.GridAdapterItem;
import dev.anvilcraft.pigsplus.network.SwitchGridAdapterModePacket;
import dev.dubhe.anvilcraft.client.init.ModKeyMappings;
import dev.anvilcraft.pigsplus.mixin.WheelLifecycleEventListenerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID, value = Dist.CLIENT)
public class GridAdapterWheelEventListener {
    private static final WheelScreenController CONTROLLER = new WheelScreenController();

    private static long keyTime = -1L;
    private static boolean keyWasDown = false;
    private static @Nullable WheelMenuModel wheelCache = null;

    private GridAdapterWheelEventListener() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;
        long gameTime = level.getGameTime();
        if (keyTime <= 0 || gameTime - keyTime <= 4) return;
        if (wheelCache == null) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof GridAdapterItem)) return;
            wheelCache = getWheel(stack);
        }
        CONTROLLER.onHoldKeyPressed(wheelCache);
        keyWasDown = true;
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (ModKeyMappings.SWITCH_TOOL_MODE.get().matches(event.getKey(), event.getScanCode())) {
            processPress(client, event.getAction());
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (ModKeyMappings.SWITCH_TOOL_MODE.get().matchesMouse(event.getButton())) {
            processPress(client, event.getAction());
        }
    }

    private static void processPress(Minecraft client, int action) {
        if (client.level == null) return;
        if (action == GLFW.GLFW_RELEASE) {
            if (keyWasDown) {
                CONTROLLER.onHoldKeyReleased();
            }
            keyWasDown = false;
            keyTime = -1L;
            wheelCache = null;
            return;
        }
        if (Minecraft.getInstance().screen != null) return;
        if (action == GLFW.GLFW_PRESS && !keyWasDown) {
            keyTime = client.level.getGameTime();
        }
    }

    private static ItemStack withMode(ItemStack holding, int mode) {
        ItemStack copied = holding.copy();
        copied.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(mode));
        return copied;
    }

    private static WheelMenuModel getWheel(ItemStack holding) {
        return WheelMenuBuilder.create()
            .slotsPerPage(2)
            .action(
                "input",
                Component.translatable("screen.anvilcraft_pigsplus.grid_adapter.input"),
                (graphics, pose, width, height) -> WheelLifecycleEventListenerAccessor.anvilcraft$renderWheelItem(graphics, withMode(holding, GridAdapterItem.INPUT_MODE)),
                ctx -> PacketDistributor.sendToServer(
                    new SwitchGridAdapterModePacket(GridAdapterItem.INPUT_MODE)
                )
            )
            .action(
                "output",
                Component.translatable("screen.anvilcraft_pigsplus.grid_adapter.output"),
                (graphics, pose, width, height) -> WheelLifecycleEventListenerAccessor.anvilcraft$renderWheelItem(graphics, withMode(holding, GridAdapterItem.OUTPUT_MODE)),
                ctx -> PacketDistributor.sendToServer(
                    new SwitchGridAdapterModePacket(GridAdapterItem.OUTPUT_MODE)
                )
            )
            .build();
    }
}
