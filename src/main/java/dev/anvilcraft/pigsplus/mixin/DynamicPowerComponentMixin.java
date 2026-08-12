package dev.anvilcraft.pigsplus.mixin;

import dev.anvilcraft.pigsplus.item.PortableWirelessChargerItem;
import dev.dubhe.anvilcraft.api.power.DynamicPowerComponent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicPowerComponent.class)
public abstract class DynamicPowerComponentMixin {
    @Inject(method = "gridTick", at = @At("HEAD"))
    private void pigsplus$gridTick(CallbackInfo ci) {
        DynamicPowerComponent component = (DynamicPowerComponent) (Object) this;
        if (component.getOwner() instanceof ServerPlayer player) {
            PortableWirelessChargerItem.onGridTick(player, component.getPowerGrid());
        }
    }
}
