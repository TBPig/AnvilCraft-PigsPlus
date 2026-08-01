package dev.anvilcraft.pigsplus.util;

import dev.anvilcraft.pigsplus.block.entity.megastructure.PlanetaryReformerHandler;
import dev.anvilcraft.pigsplus.block.entity.megastructure.StarReformerHandler;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;

public final class CelestialReformerHooks {
    private CelestialReformerHooks() {
    }

    public static boolean isActive(CelestialForgingAnvilBlockEntity cfa) {
        return isActive(cfa, -1);
    }

    public static boolean isActive(CelestialForgingAnvilBlockEntity cfa, int ring) {
        var option = cfa.getActiveMegastructureOption();
        String name = option == null ? null : option.megastructure();
        return option != null
               && (PlanetaryReformerHandler.NAME.equals(name) || StarReformerHandler.NAME.equals(name))
               && (ring < 0 || option.ring() == ring);
    }

}
