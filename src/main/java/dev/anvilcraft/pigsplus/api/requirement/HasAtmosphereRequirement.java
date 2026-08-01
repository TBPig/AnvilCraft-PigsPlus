package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import net.minecraft.network.chat.Component;

public class HasAtmosphereRequirement extends ReformerRequirement {
    @Override
    public Component getDescription() {
        return this.text("requirement.anvilcraft_pigsplus.has_atmosphere");
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (body instanceof RockyPlanetData rp) return rp.hasAtmosphere();
        if (body instanceof SpecialCelestialBodyData sp) return sp.hasAtmosphere();
        return false;
    }
}
