package dev.anvilcraft.pigsplus.client.event;

import dev.anvilcraft.lib.v2.wheel.api.WheelMenuBuilder;
import dev.anvilcraft.lib.v2.wheel.api.WheelMenuModel;
import dev.anvilcraft.lib.v2.wheel.client.input.WheelScreenController;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.BlockBreakerStaffItem;
import dev.anvilcraft.pigsplus.network.SwitchBlockBreakerStaffModePacket;
import dev.dubhe.anvilcraft.client.init.ModKeyMappings;
import dev.dubhe.anvilcraft.client.renderer.item.ItemSlotClipping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID, value = Dist.CLIENT)
public class BlockBreakerStaffWheelEventListener {
    private static final WheelScreenController CONTROLLER = new WheelScreenController();

    private static long keyTime = -1L;
    private static boolean keyWasDown = false;
    private static @Nullable WheelMenuModel wheelCache = null;

    private BlockBreakerStaffWheelEventListener() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        if (level == null) return;
        long gameTime = level.getGameTime();
        if (keyTime <= 0 || gameTime - keyTime <= 4) return;
        if (wheelCache == null) {
            LocalPlayer player = client.player;
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof BlockBreakerStaffItem)) return;
            wheelCache = getWheel();
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
        if (client.screen != null) return;
        if (action == GLFW.GLFW_PRESS && !keyWasDown) {
            keyTime = client.level.getGameTime();
        }
    }

    private static WheelMenuModel getWheel() {
        return WheelMenuBuilder.create()
            .slotsPerPage(2)
            .action(
                "protect_containers",
                Component.translatable("screen.anvilcraft_pigsplus.block_breaker_staff.protect_containers"),
                (graphics, pose, width, height) -> renderWheelItem(graphics, new ItemStack(Items.CHEST)),
                ctx -> PacketDistributor.sendToServer(new SwitchBlockBreakerStaffModePacket(true))
            )
            .action(
                "break_containers",
                Component.translatable("screen.anvilcraft_pigsplus.block_breaker_staff.break_containers"),
                (graphics, pose, width, height) -> renderWheelItem(graphics, new ItemStack(Items.IRON_PICKAXE)),
                ctx -> PacketDistributor.sendToServer(new SwitchBlockBreakerStaffModePacket(false))
            )
            .build();
    }

    public static void renderWheelItem(GuiGraphics graphics, ItemStack stack) {
        ItemSlotClipping.runWithoutClip(() -> graphics.renderItem(stack, -8, -8));
    }
}
