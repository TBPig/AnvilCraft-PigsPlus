package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HasLiquidRequirement extends ReformerRequirement {
    @Override
    public Component getDescription() {
        return this.text("requirement.anvilcraft_pigsplus.has_liquid");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("sea");
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (body instanceof RockyPlanetData rp) return rp.liquidCoverage() != LiquidCoverage.NONE;
        if (body instanceof SpecialCelestialBodyData sp) {
            return sp.liquidCoverage() != null && sp.liquidCoverage() != LiquidCoverage.NONE;
        }
        return false;
    }
}
