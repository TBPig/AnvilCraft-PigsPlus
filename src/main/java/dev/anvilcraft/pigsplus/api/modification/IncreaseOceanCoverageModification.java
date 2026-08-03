package dev.anvilcraft.pigsplus.api.modification;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyClass;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * 提升一级指定液体的海洋覆盖度，并重新生成行星资源。
 */
public class IncreaseOceanCoverageModification extends ReformerModification {
    public static final MapCodec<IncreaseOceanCoverageModification> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("ocean_fluid")
                .forGetter(IncreaseOceanCoverageModification::getOceanFluid)
        ).apply(instance, IncreaseOceanCoverageModification::new));

    private final ResourceLocation oceanFluid;

    public IncreaseOceanCoverageModification() {
        this(ResourceLocation.withDefaultNamespace("water"));
    }

    public IncreaseOceanCoverageModification(ResourceLocation oceanFluid) {
        this.oceanFluid = oceanFluid;
    }

    public ResourceLocation getOceanFluid() {
        return this.oceanFluid;
    }

    @Override
    public MapCodec<? extends ReformerModification> codec() {
        return CODEC;
    }

    @Override
    public Component getDescription() {
        Fluid fluid = BuiltInRegistries.FLUID.get(this.oceanFluid);
        Component fluidName = fluid.isSame(Fluids.EMPTY)
            ? Component.literal(this.oceanFluid.toString())
            : fluid.getFluidType().getDescription();
        return this.text("modification.anvilcraft_pigsplus.increase_ocean_coverage", fluidName);
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("sea");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (!(body instanceof RockyPlanetData rp)) return;

        LiquidCoverage next = switch (rp.liquidCoverage()) {
            case NONE -> LiquidCoverage.LOW;
            case LOW -> LiquidCoverage.MEDIUM;
            case MEDIUM -> LiquidCoverage.HIGH;
            case HIGH -> null;
        };
        if (next == null) return;

        CelestialBodyClass nextClass = switch (next) {
            case LOW -> CelestialBodyClass.ROCKY_LOW_LIQUID;
            case MEDIUM -> CelestialBodyClass.ROCKY_MED_LIQUID;
            case HIGH -> CelestialBodyClass.ROCKY_HIGH_LIQUID;
            default -> rp.bodyClass();
        };
        RockyPlanetData updated = new RockyPlanetData(
            nextClass,
            rp.hasAtmosphere(),
            next,
            rp.temperature(),
            rp.ringType(),
            rp.size(),
            rp.paletteBaseRow(),
            rp.paletteOverlayRow(),
            rp.axialTilt(),
            rp.rotationSpeed(),
            rp.magneticFieldStrength()
        );
        CelestialReformerPlanetUtil.regenerate(be, updated, this.oceanFluid);
    }
}
