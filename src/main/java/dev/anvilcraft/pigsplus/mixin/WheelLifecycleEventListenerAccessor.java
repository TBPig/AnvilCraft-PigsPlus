package dev.anvilcraft.pigsplus.mixin;

import dev.dubhe.anvilcraft.client.event.WheelLifecycleEventListener;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WheelLifecycleEventListener.class)
public interface WheelLifecycleEventListenerAccessor {
    @Invoker("renderWheelItem")
    static void anvilcraft$renderWheelItem(GuiGraphics graphics, ItemStack stack) {
        throw new AssertionError();
    }
}