package dev.anvilcraft.pigsplus.block.entity.megastructure;

import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe.LaserType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record CelestialReformerInputRequirement(
    CelestialReformerInputChannel channel,
    ResourceLocation resource,
    int amount,
    @Nullable LaserType laserType
) {
    public static CelestialReformerInputRequirement logisticsItem(ResourceLocation item, int amount) {
        return new CelestialReformerInputRequirement(
            CelestialReformerInputChannel.LOGISTICS_ITEM, item, amount, null
        );
    }

    public static CelestialReformerInputRequirement fluid(ResourceLocation fluid, int amount) {
        return new CelestialReformerInputRequirement(
            CelestialReformerInputChannel.FLUID_INTERFACE, fluid, amount, null
        );
    }

    public static CelestialReformerInputRequirement laser(int level) {
        return new CelestialReformerInputRequirement(
            CelestialReformerInputChannel.LASER_INTERFACE,
            ResourceLocation.withDefaultNamespace("laser"),
            level,
            LaserType.ANY
        );
    }

    public static CelestialReformerInputRequirement laser(int level, boolean gamma) {
        return laser(level, gamma ? LaserType.GAMMA : LaserType.NORMAL);
    }

    public static CelestialReformerInputRequirement laser(int level, LaserType type) {
        return new CelestialReformerInputRequirement(
            CelestialReformerInputChannel.LASER_INTERFACE,
            ResourceLocation.withDefaultNamespace("laser"),
            level,
            type
        );
    }
}
