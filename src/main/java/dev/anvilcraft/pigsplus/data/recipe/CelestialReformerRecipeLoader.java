package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonFluids;
import dev.anvilcraft.pigsplus.init.PigsReformerModifications;
import dev.anvilcraft.pigsplus.init.PigsReformerRequirements;
import dev.anvilcraft.pigsplus.api.requirement.LiquidCoverageRangeRequirement;
import dev.anvilcraft.pigsplus.api.requirement.MagneticFieldRequirement;
import dev.anvilcraft.pigsplus.api.requirement.RotationSpeedRequirement;
import dev.anvilcraft.pigsplus.api.requirement.TemperatureRangeRequirement;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.Temperature;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.block.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class CelestialReformerRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.SLOW_ROTATION)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(
                PigsReformerRequirements.ROTATION_SPEED,
                new RotationSpeedRequirement(1, null)
            )
            .item(ModBlocks.LEAD_BLOCK, 640)
            .save(provider, "slow_rotation");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.FAST_ROTATION)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(
                PigsReformerRequirements.ROTATION_SPEED,
                new RotationSpeedRequirement(null, 3)
            )
            .item(ModBlocks.SUGAR_BLOCK, 640)
            .save(provider, "fast_rotation");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.STRENGTHEN_MAGNETIC_FIELD)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(
                PigsReformerRequirements.MAGNETIC_FIELD,
                new MagneticFieldRequirement(null, 3)
            )
            .item(ModBlocks.MAGNET_BLOCK, 640)
            .save(provider, "strengthen_magnetic_field");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.WEAKEN_MAGNETIC_FIELD)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(
                PigsReformerRequirements.MAGNETIC_FIELD,
                new MagneticFieldRequirement(1, null)
            )
            .item(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 640)
            .save(provider, "weaken_magnetic_field");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.INCREASE_LIQUID_COVERAGE)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(PigsReformerRequirements.WATER_OCEAN)
            .fluid(ResourceLocation.withDefaultNamespace("water"), 320)
            .save(provider, "increase_liquid_coverage");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.INCREASE_TEMPERATURE)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(
                PigsReformerRequirements.TEMPERATURE_RANGE,
                new TemperatureRangeRequirement(Temperature.FREEZING, Temperature.HOT)
            )
            .item(ModBlocks.HEATED_TUNGSTEN_BLOCK, 320)
            .save(provider, "increase_temperature");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.DECREASE_TEMPERATURE)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(
                PigsReformerRequirements.TEMPERATURE_RANGE,
                new TemperatureRangeRequirement(Temperature.COLD, Temperature.SCORCHED)
            )
            .item(Blocks.BLUE_ICE, 320)
            .save(provider, "decrease_temperature");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.ADD_BIOLOGICAL_RESOURCES)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(PigsReformerRequirements.HAS_ATMOSPHERE)
            .requirement(PigsReformerRequirements.HAS_LIQUID)
            .requirement(
                PigsReformerRequirements.LIQUID_COVERAGE_RANGE,
                new LiquidCoverageRangeRequirement(LiquidCoverage.NONE, LiquidCoverage.MEDIUM)
            )
            .requirement(
                PigsReformerRequirements.TEMPERATURE_RANGE,
                new TemperatureRangeRequirement(Temperature.MILD, Temperature.MILD)
            )
            .item(Blocks.MOSS_BLOCK, 320)
            .item(Blocks.BONE_BLOCK, 320)
            .save(provider, "add_biological_resources");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.ADD_CIVILIZATION)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(PigsReformerRequirements.HAS_BIOLOGICAL_RESOURCES)
            .requirement(
                PigsReformerRequirements.LIQUID_COVERAGE_RANGE,
                new LiquidCoverageRangeRequirement(LiquidCoverage.MEDIUM, LiquidCoverage.MEDIUM)
            )
            .fluid(ModFluids.EXP_FLUID.getId(), 320)
            .save(provider, "add_civilization");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.WASTELAND)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(PigsReformerRequirements.HAS_CIVILIZATION)
            .item(ModBlocks.PLUTONIUM_BLOCK, 320)
            .save(provider, "wasteland");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.VOID_WASTELAND)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(PigsReformerRequirements.HAS_CIVILIZATION)
            .fluid(AddonFluids.VOID_ACID.getId(), 320)
            .save(provider, "void_wasteland");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.SLOW_ROTATION)
            .requirement(PigsReformerRequirements.STAR_REFORMER)
            .requirement(
                PigsReformerRequirements.ROTATION_SPEED,
                new RotationSpeedRequirement(1, null)
            )
            .item(ModBlocks.LEAD_BLOCK, 3200)
            .laser(16)
            .save(provider, "star_slow_rotation");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.FAST_ROTATION)
            .requirement(PigsReformerRequirements.STAR_REFORMER)
            .requirement(
                PigsReformerRequirements.ROTATION_SPEED,
                new RotationSpeedRequirement(null, 3)
            )
            .item(ModBlocks.SUGAR_BLOCK, 3200)
            .laser(16)
            .save(provider, "star_fast_rotation");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.STRENGTHEN_MAGNETIC_FIELD)
            .requirement(PigsReformerRequirements.STAR_REFORMER)
            .requirement(
                PigsReformerRequirements.MAGNETIC_FIELD,
                new MagneticFieldRequirement(null, 3)
            )
            .item(ModBlocks.MAGNET_BLOCK, 3200)
            .laser(16)
            .save(provider, "star_strengthen_magnetic_field");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.WEAKEN_MAGNETIC_FIELD)
            .requirement(PigsReformerRequirements.STAR_REFORMER)
            .requirement(
                PigsReformerRequirements.MAGNETIC_FIELD,
                new MagneticFieldRequirement(1, null)
            )
            .item(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 3200)
            .laser(16)
            .save(provider, "star_weaken_magnetic_field");
    }
}
