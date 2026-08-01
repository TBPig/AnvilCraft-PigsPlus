package dev.anvilcraft.pigsplus.api.modification;

import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.Temperature;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 提升一级行星温度。
 */
public class IncreaseTemperatureModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.increase_temperature");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("temperature");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (!(body instanceof RockyPlanetData rp)) return;
        Temperature next = switch (rp.temperature()) {
            case FREEZING -> Temperature.COLD;
            case COLD -> Temperature.MILD;
            case MILD -> Temperature.HOT;
            case HOT -> Temperature.SCORCHED;
            case SCORCHED -> Temperature.SCORCHED;
        };
        if (next == rp.temperature()) return;
        be.setCelestialBodyData(new RockyPlanetData(
            rp.bodyClass(),
            rp.hasAtmosphere(),
            rp.liquidCoverage(),
            next,
            rp.ringType(),
            rp.size(),
            rp.paletteBaseRow(),
            rp.paletteOverlayRow(),
            rp.axialTilt(),
            rp.rotationSpeed(),
            rp.magneticFieldStrength()
        ));
        be.setChanged();
    }
}
