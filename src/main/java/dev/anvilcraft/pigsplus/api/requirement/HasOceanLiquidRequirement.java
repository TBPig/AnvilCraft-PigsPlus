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
 * 要求行星当前海洋为指定液体。
 */
public class HasOceanLiquidRequirement extends ReformerRequirement {
    public static final MapCodec<HasOceanLiquidRequirement> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("fluid").forGetter(HasOceanLiquidRequirement::fluid)
        ).apply(instance, HasOceanLiquidRequirement::new));

    private final ResourceLocation fluid;

    public HasOceanLiquidRequirement() {
        this(ResourceLocation.withDefaultNamespace("water"));
    }

    public HasOceanLiquidRequirement(ResourceLocation fluid) {
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
        return this.text("requirement.anvilcraft_pigsplus.has_ocean_liquid", fluidName);
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
        if (!(be.getCelestialBodyData() instanceof RockyPlanetData rp)
            || rp.liquidCoverage() == LiquidCoverage.NONE) {
            return false;
        }
        return be.getPlanetaryResourceSet() != null
            && be.getPlanetaryResourceSet().getFluids().stream()
                .anyMatch(stack -> stack.fluidId().equals(this.fluid));
    }
}
