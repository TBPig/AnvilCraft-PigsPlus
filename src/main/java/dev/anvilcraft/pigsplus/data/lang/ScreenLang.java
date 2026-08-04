package dev.anvilcraft.pigsplus.data.lang;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;

public class ScreenLang {
    public static void init(RegistrumLangProvider provider) {
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.input", "Input: AnvilCraft Power -> FE");
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.output", "Output: FE -> AnvilCraft Power");
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.title", "Grid Adapter");
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.max", "MAX");
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.value", "Conversion (kW): %s / %s");
        provider.add("screen.anvilcraft.cfa.megastructure.planetary_reformer", "Planetary Reformer");
        provider.add("screen.anvilcraft.cfa.megastructure.star_reformer", "Star Reformer");
        provider.add("screen.anvilcraft.cfa.megastructure.planetary_reformer.usage", "Consume resources to reform the celestial body.");
        provider.add("screen.anvilcraft.cfa.megastructure.star_reformer.usage", "Consume resources to reform the celestial body.");
        provider.add("screen.anvilcraft.cfa.class.special.fulgora", "Fulgora");
    }
}
