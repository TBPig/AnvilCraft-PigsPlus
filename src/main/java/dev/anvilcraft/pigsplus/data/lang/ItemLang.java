package dev.anvilcraft.pigsplus.data.lang;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;

public class ItemLang {
    public static void init(RegistrumLangProvider provider) {
        provider.add("tooltip.anvilcraft_pigsplus.menger_sponge_staff", "Remove a large amount of liquid");
        provider.add("tooltip.anvilcraft_pigsplus.portable_wireless_charger", "When backpack, it consumes %d kW and generates %d FE/t");
        provider.add("tooltip.anvilcraft_pigsplus.grid_adapter.mode", "Mode: %s");
        provider.add("tooltip.anvilcraft_pigsplus.grid_adapter.use", "Right-click to configure. Shift + right-click an FE block to toggle it. Hold %s to open the mode wheel.");
        provider.add("tooltip.anvilcraft_pigsplus.grid_adapter.power", "Conversion: %s kW / max %s");
        provider.add("tooltip.anvilcraft_pigsplus.grid_adapter.configure", "Right-click to configure conversion");
        provider.add("tooltip.anvilcraft_pigsplus.wireless_transmitter.pos_set", "Will transmit to %s");
        provider.add("tooltip.anvilcraft_pigsplus.wireless_transmitter.use", "Right-click a block to bind a target, then place it against an item or fluid container");
    }
}
