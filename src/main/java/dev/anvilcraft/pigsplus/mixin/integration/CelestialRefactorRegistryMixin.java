package dev.anvilcraft.pigsplus.mixin.integration;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.megastructure.PlanetaryReformerHandler;
import dev.anvilcraft.pigsplus.block.entity.megastructure.StarReformerHandler;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialRefactorOption;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialRefactorRegistry;
import dev.dubhe.anvilcraft.block.entity.celestial.GiantPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.StarData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(CelestialRefactorRegistry.class)
public abstract class CelestialRefactorRegistryMixin {

    @Inject(method = "getOptions", at = @At("RETURN"), cancellable = true)
    private static void addPlanetaryReformer(
        CelestialBodyData body,
        boolean amplified,
        PlanetaryResourceSet resources,
        CallbackInfoReturnable<List<CelestialRefactorOption>> cir
    ) {
        try {
            if (!anvilcraft_pigsplus$isPlanet(body) && !(body instanceof StarData)) return;
            List<CelestialRefactorOption> original = cir.getReturnValue();
            if (original == null) return;
            List<CelestialRefactorOption> options = new ArrayList<>(original);
            if (options.stream().anyMatch(option -> PlanetaryReformerHandler.NAME.equals(option.megastructure()))) return;

            int ring = CelestialRefactorRegistry.getInnermostRing(body, amplified);
            if (anvilcraft_pigsplus$isPlanet(body)) {
                options.add(CelestialRefactorOption.withMaterial(
                    ring,
                    PlanetaryReformerHandler.NAME,
                    AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_1_reformer"),
                    "screen.anvilcraft.cfa.megastructure.planetary_reformer",
                    AddonItems.CELESTIAL_REFORMER_COMPONENT.asItem(),
                    16
                ));
            } else {
            options.add(CelestialRefactorOption.withMaterial(
                ring,
                StarReformerHandler.NAME,
                AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_4_reformer"),
                "screen.anvilcraft.cfa.megastructure.star_reformer",
                AddonItems.CELESTIAL_REFORMER_COMPONENT.asItem(),
                64
                ));
            }
            cir.setReturnValue(options);
        } catch (RuntimeException | LinkageError ex) {
            AnvilCraftPigsPlus.LOGGER.warn("Failed to add planetary reformer option", ex);
        }
    }

    @Unique
    private static boolean anvilcraft_pigsplus$isPlanet(CelestialBodyData body) {
        return body instanceof RockyPlanetData
               || body instanceof GiantPlanetData
               || (body instanceof SpecialCelestialBodyData special && !special.isErrorPlanet());
    }
}
