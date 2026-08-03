package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.api.modification.IncreaseOceanCoverageModification;
import dev.anvilcraft.pigsplus.api.modification.SpecialCelestialBodyModification;
import dev.anvilcraft.pigsplus.api.requirement.LiquidCoverageRangeRequirement;
import dev.anvilcraft.pigsplus.api.requirement.MagneticFieldRequirement;
import dev.anvilcraft.pigsplus.api.requirement.OceanLiquidRequirement;
import dev.anvilcraft.pigsplus.api.requirement.RotationSpeedRequirement;
import dev.anvilcraft.pigsplus.api.requirement.TemperatureRangeRequirement;
import dev.anvilcraft.pigsplus.init.AddonFluids;
import dev.anvilcraft.pigsplus.init.PigsReformerModifications;
import dev.anvilcraft.pigsplus.init.PigsReformerRequirements;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.Temperature;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.block.ModFluids;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
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

        ocean(provider, ResourceLocation.withDefaultNamespace("water"), "increase_liquid_coverage");
        ocean(provider, ResourceLocation.withDefaultNamespace("lava"), "increase_lava_coverage");
        ocean(provider, ResourceLocation.withDefaultNamespace("milk"), "increase_milk_coverage");
        ocean(provider, ModFluids.OIL.getId(), "increase_oil_coverage");
        ocean(provider, ModFluids.MELT_GEM.getId(), "increase_melt_gem_coverage");

        CelestialReformerRecipe.builder()
            .modification(PigsReformerModifications.DECREASE_LIQUID_COVERAGE)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(PigsReformerRequirements.HAS_LIQUID)
            .item(Items.SPONGE, 320)
            .save(provider, "decrease_liquid_coverage");

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
            .modification(PigsReformerModifications.ADD_ATMOSPHERE)
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(PigsReformerRequirements.NO_ATMOSPHERE)
            .item(Items.GLASS_BOTTLE, 320)
            .save(provider, "add_atmosphere");

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
            .item(Blocks.BONE_BLOCK, 320)
            .item(Blocks.MOSS_BLOCK, 320)
            .item(Items.COD, 320)
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
            .item(Items.BOOK, 1280)
            .fluid(ModFluids.EXP_FLUID.getId(), 1280000)
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
            .fluid(AddonFluids.VOID_ACID.getId(), 320000)
            .save(provider, "void_wasteland");

        CelestialReformerRecipe.builder()
            .modification(
                PigsReformerModifications.SPECIAL_CELESTIAL_BODY,
                new SpecialCelestialBodyModification(AnvilCraftPigsPlus.of("special_celestial_body/fulgora"))
            )
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .item(ModItems.PROCESSOR, 1280)
            .item(Blocks.LIGHTNING_ROD, 1280)
            .item(Items.STONE, 6400)
            .fluid(ModFluids.OIL.getId(), 640000)
            .laser(64)
            .save(provider, "fulgora");

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

    public static void ocean(RegistrumRecipeProvider provider, ResourceLocation fluid, String recipeId) {
        CelestialReformerRecipe.builder()
            .modification(
                PigsReformerModifications.INCREASE_OCEAN_COVERAGE,
                new IncreaseOceanCoverageModification(fluid)
            )
            .requirement(PigsReformerRequirements.PLANETARY_REFORMER)
            .requirement(PigsReformerRequirements.ROCKY_PLANET)
            .requirement(
                PigsReformerRequirements.OCEAN_LIQUID,
                new OceanLiquidRequirement(fluid)
            )
            .fluid(fluid, 320000)
            .save(provider, recipeId);
    }
}
