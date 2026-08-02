package dev.anvilcraft.pigsplus.api.modification;

import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 为岩石行星添加大气层。
 */
public class AddAtmosphereModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.add_atmosphere");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("atmosphere");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (!(body instanceof RockyPlanetData rp) || rp.hasAtmosphere()) return;
        be.setCelestialBodyData(new RockyPlanetData(
            rp.bodyClass(),
            true,
            rp.liquidCoverage(),
            rp.temperature(),
            rp.ringType(),
            rp.size(),
            rp.paletteBaseRow(),
            rp.paletteOverlayRow(),
            rp.axialTilt(),
            rp.rotationSpeed(),
            rp.magneticFieldStrength()
        ));
    }
}
