package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluids;

/**
 * 要求无液体覆盖，或当前液体为水且覆盖度未满。
 */
public class WaterOceanRequirement extends ReformerRequirement {
    @Override
    public Component getDescription() {
        return this.text("requirement.anvilcraft_pigsplus.water_ocean");
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        if (!(be.getCelestialBodyData() instanceof RockyPlanetData rp)) return false;
        if (rp.liquidCoverage() == LiquidCoverage.NONE) return true;
        if (rp.liquidCoverage() == LiquidCoverage.HIGH) return false;
        if (be.getPlanetaryResourceSet() == null) return false;
        var water = BuiltInRegistries.FLUID.getKey(Fluids.WATER);
        return be.getPlanetaryResourceSet().getFluids().stream()
            .anyMatch(stack -> stack.fluidId().equals(water));
    }
}
