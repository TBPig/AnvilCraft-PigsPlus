package dev.anvilcraft.pigsplus.mixin.integration;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.megastructure.PlanetaryReformerHandler;
import dev.anvilcraft.pigsplus.block.entity.megastructure.ReformerHandler;
import dev.anvilcraft.pigsplus.block.entity.megastructure.StarReformerHandler;
import dev.dubhe.anvilcraft.block.entity.CfaMegastructureManager;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.megastructure.IMegastructureHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CfaMegastructureManager.class)
public abstract class CfaMegastructureManagerMixin {
    @Shadow
    private void registerHandler(IMegastructureHandler handler) {
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addPlanetaryReformer(CallbackInfo ci) {
        try {
            registerHandler(new PlanetaryReformerHandler());
            registerHandler(new StarReformerHandler());
        } catch (RuntimeException | LinkageError ex) {
            AnvilCraftPigsPlus.LOGGER.warn("Failed to register planetary reformer handler", ex);
        }
    }

    @Inject(method = "syncLaserRequirements", at = @At("TAIL"))
    private void syncPlanetaryReformerLaserRequirements(CelestialForgingAnvilBlockEntity anvil, CallbackInfo ci) {
        IMegastructureHandler active = ((CfaMegastructureManager) (Object) this).getActiveHandler(anvil);
        if (active instanceof ReformerHandler handler) {
            handler.syncLaserRequirements(anvil);
        }
    }
}
