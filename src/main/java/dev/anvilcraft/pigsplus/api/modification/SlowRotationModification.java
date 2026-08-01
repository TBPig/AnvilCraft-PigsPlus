package dev.anvilcraft.pigsplus.api.modification;

import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.GiantPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.StarData;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SlowRotationModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.slow_rotation");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (body == null) return;
        int newRotation = Mth.clamp(body.rotationSpeed() - 1, MIN_LEVEL, MAX_LEVEL);
        CelestialBodyData updated = switch (body) {
            case RockyPlanetData rp -> new RockyPlanetData(
                rp.bodyClass(), rp.hasAtmosphere(), rp.liquidCoverage(), rp.temperature(),
                rp.ringType(), rp.size(), rp.paletteBaseRow(), rp.paletteOverlayRow(),
                rp.axialTilt(), newRotation, rp.magneticFieldStrength()
            );
            case GiantPlanetData gp -> new GiantPlanetData(
                gp.bodyClass(), gp.pressureType(), gp.windSpeed(), gp.ringType(),
                gp.size(), gp.paletteBaseRow(), gp.paletteOverlayRow(),
                gp.axialTilt(), newRotation, gp.magneticFieldStrength(), gp.brownDwarf()
            );
            case SpecialCelestialBodyData sp -> new SpecialCelestialBodyData(
                sp.recipeId(), sp.name(), sp.size(), sp.axialTilt(),
                newRotation, sp.magneticFieldStrength(),
                sp.temperature(), sp.hasAtmosphere(), sp.liquidCoverage(),
                sp.isErrorPlanet(), sp.needsCustomModel(), sp.model(), sp.playerHeadProfile()
            );
            case StarData star -> new StarData(
                star.bodyClass(), star.size(), star.colorR(), star.colorG(), star.colorB(),
                star.axialTilt(), newRotation, star.magneticFieldStrength(),
                star.energy(), star.bodyUuid()
            );
            default -> null;
        };
        if (updated == null) return;
        be.setCelestialBodyData(updated);
    }
}
