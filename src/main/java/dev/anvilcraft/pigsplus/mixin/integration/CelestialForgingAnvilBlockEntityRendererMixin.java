package dev.anvilcraft.pigsplus.mixin.integration;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.megastructure.StarReformerHandler;
import dev.anvilcraft.pigsplus.util.CelestialReformerHooks;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.CelestialForgingAnvilBlockEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CelestialForgingAnvilBlockEntityRenderer.class)
public abstract class CelestialForgingAnvilBlockEntityRendererMixin {
    @Unique
    private static final ModelResourceLocation PLANETARY_REFORMER_MODEL = ModelResourceLocation.standalone(
        AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_1_reformer")
    );
    @Unique
    private static final ModelResourceLocation STAR_REFORMER_MODEL = ModelResourceLocation.standalone(
        AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_4_reformer")
    );

    @Inject(method = "getRing1Model", at = @At("HEAD"), cancellable = true)
    private void replaceRing1Model(
        CelestialForgingAnvilBlockEntity anvil,
        CallbackInfoReturnable<ModelResourceLocation> cir
    ) {
        anvilcraft_pigsplus$setReformerModel(anvil, cir, 1);
    }

    @Inject(method = "getRing2Model", at = @At("HEAD"), cancellable = true)
    private void replaceRing2Model(
        CelestialForgingAnvilBlockEntity anvil,
        CallbackInfoReturnable<ModelResourceLocation> cir
    ) {
        anvilcraft_pigsplus$setReformerModel(anvil, cir, 2);
    }

    @Inject(method = "getRing4Model", at = @At("HEAD"), cancellable = true)
    private void replaceRing4Model(
        CelestialForgingAnvilBlockEntity anvil,
        CallbackInfoReturnable<ModelResourceLocation> cir
    ) {
        anvilcraft_pigsplus$setReformerModel(anvil, cir, 4);
    }

    @Inject(method = "getRing5Model", at = @At("HEAD"), cancellable = true)
    private void replaceRing5Model(
        CelestialForgingAnvilBlockEntity anvil,
        CallbackInfoReturnable<ModelResourceLocation> cir
    ) {
        anvilcraft_pigsplus$setReformerModel(anvil, cir, 5);
    }

    @Unique
    private static void anvilcraft_pigsplus$setReformerModel(
        CelestialForgingAnvilBlockEntity anvil,
        CallbackInfoReturnable<ModelResourceLocation> cir,
        int ring
    ) {
        if (CelestialReformerHooks.isActive(anvil, ring)) {
            cir.setReturnValue(anvilcraft_pigsplus$isStarReformer(anvil) ? STAR_REFORMER_MODEL : PLANETARY_REFORMER_MODEL);
        }
    }

    @Unique
    private static boolean anvilcraft_pigsplus$isStarReformer(CelestialForgingAnvilBlockEntity anvil) {
        var option = anvil.getActiveMegastructureOption();
        return option != null && StarReformerHandler.NAME.equals(option.megastructure());
    }
}
