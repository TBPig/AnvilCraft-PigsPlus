package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * 要求无液体覆盖，或当前海洋为指定液体且覆盖度未满。
 */
public class OceanLiquidRequirement extends ReformerRequirement {
    public static final MapCodec<OceanLiquidRequirement> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("fluid").forGetter(OceanLiquidRequirement::fluid)
        ).apply(instance, OceanLiquidRequirement::new));

    private final ResourceLocation fluid;

    public OceanLiquidRequirement() {
        this(ResourceLocation.withDefaultNamespace("water"));
    }

    public OceanLiquidRequirement(ResourceLocation fluid) {
        this.fluid = fluid;
    }

    public ResourceLocation fluid() {
        return this.fluid;
    }

    @Override
    public Component getDescription() {
        Fluid fluid = BuiltInRegistries.FLUID.get(this.fluid);
        Component fluidName = fluid.isSame(Fluids.EMPTY)
            ? Component.literal(this.fluid.toString())
            : fluid.getFluidType().getDescription();
        return this.text("requirement.anvilcraft_pigsplus.ocean_liquid", fluidName);
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("sea");
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return CODEC;
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        if (!(be.getCelestialBodyData() instanceof RockyPlanetData rp)) return false;
        if (rp.liquidCoverage() == LiquidCoverage.NONE) return true;
        if (rp.liquidCoverage() == LiquidCoverage.HIGH) return false;
        return be.getPlanetaryResourceSet() != null
            && be.getPlanetaryResourceSet().getFluids().stream()
                .anyMatch(stack -> stack.fluidId().equals(this.fluid));
    }
}
