package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.api.requirement.CelestialReformerRequirements;
import dev.anvilcraft.pigsplus.api.requirement.HasAtmosphereRequirement;
import dev.anvilcraft.pigsplus.api.requirement.HasBiologicalResourcesRequirement;
import dev.anvilcraft.pigsplus.api.requirement.HasCivilizationRequirement;
import dev.anvilcraft.pigsplus.api.requirement.HasLiquidRequirement;
import dev.anvilcraft.pigsplus.api.requirement.HasOceanLiquidRequirement;
import dev.anvilcraft.pigsplus.api.requirement.HasOtherMegastructureRequirement;
import dev.anvilcraft.pigsplus.api.requirement.LiquidCoverageRangeRequirement;
import dev.anvilcraft.pigsplus.api.requirement.MagneticFieldRequirement;
import dev.anvilcraft.pigsplus.api.requirement.NoAtmosphereRequirement;
import dev.anvilcraft.pigsplus.api.requirement.NoLiquidRequirement;
import dev.anvilcraft.pigsplus.api.requirement.NoOtherMegastructureRequirement;
import dev.anvilcraft.pigsplus.api.requirement.OceanLiquidRequirement;
import dev.anvilcraft.pigsplus.api.requirement.PlanetaryReformerRequirement;
import dev.anvilcraft.pigsplus.api.requirement.ReformerRequirement;
import dev.anvilcraft.pigsplus.api.requirement.RockyPlanetRequirement;
import dev.anvilcraft.pigsplus.api.requirement.RotationSpeedRequirement;
import dev.anvilcraft.pigsplus.api.requirement.StarReformerRequirement;
import dev.anvilcraft.pigsplus.api.requirement.TemperatureRangeRequirement;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PigsReformerRequirements {
    private static final DeferredRegister<ReformerRequirement> DF = DeferredRegister.create(
        CelestialReformerRequirements.REGISTRY,
        AnvilCraftPigsPlus.MOD_ID
    );

    public static final DeferredHolder<ReformerRequirement, StarReformerRequirement> STAR_REFORMER =
        DF.register("star_reformer", StarReformerRequirement::new);
    public static final DeferredHolder<ReformerRequirement, PlanetaryReformerRequirement> PLANETARY_REFORMER =
        DF.register("planetary_reformer", PlanetaryReformerRequirement::new);
    public static final DeferredHolder<ReformerRequirement, HasLiquidRequirement> HAS_LIQUID =
        DF.register("has_liquid", HasLiquidRequirement::new);
    public static final DeferredHolder<ReformerRequirement, NoLiquidRequirement> NO_LIQUID =
        DF.register("no_liquid", NoLiquidRequirement::new);
    public static final DeferredHolder<ReformerRequirement, HasAtmosphereRequirement> HAS_ATMOSPHERE =
        DF.register("has_atmosphere", HasAtmosphereRequirement::new);
    public static final DeferredHolder<ReformerRequirement, NoAtmosphereRequirement> NO_ATMOSPHERE =
        DF.register("no_atmosphere", NoAtmosphereRequirement::new);
    public static final DeferredHolder<ReformerRequirement, RotationSpeedRequirement> ROTATION_SPEED =
        DF.register("rotation_speed", RotationSpeedRequirement::new);
    public static final DeferredHolder<ReformerRequirement, MagneticFieldRequirement> MAGNETIC_FIELD =
        DF.register("magnetic_field", MagneticFieldRequirement::new);
    public static final DeferredHolder<ReformerRequirement, RockyPlanetRequirement> ROCKY_PLANET =
        DF.register("rocky_planet", RockyPlanetRequirement::new);
    public static final DeferredHolder<ReformerRequirement, OceanLiquidRequirement> WATER_OCEAN =
        DF.register("water_ocean", () -> new OceanLiquidRequirement(ResourceLocation.withDefaultNamespace("water")));
    public static final DeferredHolder<ReformerRequirement, OceanLiquidRequirement> OCEAN_LIQUID =
        DF.register("ocean_liquid", () -> new OceanLiquidRequirement());
    public static final DeferredHolder<ReformerRequirement, HasOceanLiquidRequirement> HAS_OCEAN_LIQUID =
        DF.register("has_ocean_liquid", () -> new HasOceanLiquidRequirement());
    public static final DeferredHolder<ReformerRequirement, TemperatureRangeRequirement> TEMPERATURE_RANGE =
        DF.register("temperature_range", TemperatureRangeRequirement::new);
    public static final DeferredHolder<ReformerRequirement, LiquidCoverageRangeRequirement> LIQUID_COVERAGE_RANGE =
        DF.register("liquid_coverage_range", LiquidCoverageRangeRequirement::new);
    public static final DeferredHolder<ReformerRequirement, HasBiologicalResourcesRequirement> HAS_BIOLOGICAL_RESOURCES =
        DF.register("has_biological_resources", HasBiologicalResourcesRequirement::new);
    public static final DeferredHolder<ReformerRequirement, HasCivilizationRequirement> HAS_CIVILIZATION =
        DF.register("has_civilization", HasCivilizationRequirement::new);
    public static final DeferredHolder<ReformerRequirement, HasOtherMegastructureRequirement>
        HAS_OTHER_MEGASTRUCTURE = DF.register("has_other_megastructure", HasOtherMegastructureRequirement::new);
    public static final DeferredHolder<ReformerRequirement, NoOtherMegastructureRequirement>
        NO_OTHER_MEGASTRUCTURE = DF.register("no_other_megastructure", NoOtherMegastructureRequirement::new);

    public static void register(IEventBus bus) {
        DF.register(bus);
    }
}
