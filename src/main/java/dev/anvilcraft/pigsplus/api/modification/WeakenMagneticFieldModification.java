package dev.anvilcraft.pigsplus.api.modification;

import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.GiantPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.StarData;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class WeakenMagneticFieldModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.weaken_magnetic_field");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (body == null) return;
        int newMagnetic = Mth.clamp(body.magneticFieldStrength() - 1, MIN_LEVEL, MAX_LEVEL);
        CelestialBodyData updated = switch (body) {
            case RockyPlanetData rp -> new RockyPlanetData(
                rp.bodyClass(), rp.hasAtmosphere(), rp.liquidCoverage(), rp.temperature(),
                rp.ringType(), rp.size(), rp.paletteBaseRow(), rp.paletteOverlayRow(),
                rp.axialTilt(), rp.rotationSpeed(), newMagnetic
            );
            case GiantPlanetData gp -> new GiantPlanetData(
                gp.bodyClass(), gp.pressureType(), gp.windSpeed(), gp.ringType(),
                gp.size(), gp.paletteBaseRow(), gp.paletteOverlayRow(),
                gp.axialTilt(), gp.rotationSpeed(), newMagnetic, gp.brownDwarf()
            );
            case SpecialCelestialBodyData sp -> new SpecialCelestialBodyData(
                sp.recipeId(), sp.name(), sp.size(), sp.axialTilt(),
                sp.rotationSpeed(), newMagnetic,
                sp.temperature(), sp.hasAtmosphere(), sp.liquidCoverage(),
                sp.isErrorPlanet(), sp.needsCustomModel(), sp.model(), sp.playerHeadProfile()
            );
            case StarData star -> new StarData(
                star.bodyClass(), star.size(), star.colorR(), star.colorG(), star.colorB(),
                star.axialTilt(), star.rotationSpeed(), newMagnetic,
                star.energy(), star.bodyUuid()
            );
            default -> null;
        };
        if (updated == null) return;
        be.setCelestialBodyData(updated);
    }
}
