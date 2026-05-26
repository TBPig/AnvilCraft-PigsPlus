package dev.anvilcraft.pigsplus.data.lang;

import dev.anvilcraft.lib.v2.config.ConfigData;
import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;
import dev.anvilcraft.pigsplus.config.AddonServerConfig;

public class LangHandler {
    /**
     * language file init
     *
     * @param provider provider
     */
    public static void init(RegistrumLangProvider provider) {
        ConfigData.readConfigClass(provider, AddonServerConfig.class);
        JeiLang.init(provider);

        provider.add(
            "block.anvilcraft_pigsplus.enchanted_generator.placement_too_close_to_another",
            "Too close to another enchanted generator"
        );

        provider.add("tooltip.anvilcraft_pigsplus.enchanted_generator.time", "Remaining time: %s");
        provider.add("tooltip.anvilcraft_pigsplus.enchanted_generator.max_power_value", "Max power consumption: %d kW");
        provider.add("tooltip.anvilcraft_pigsplus.enchanted_generator.power_rate", "Power consumption rate: %s");
        provider.add("tooltip.anvilcraft_pigsplus.enchanted_generator.previous_energy_consumption", "Previous item energy consumption: %d");
        provider.add("tooltip.anvilcraft_pigsplus.portable_wireless_charger", "When backpack, it consumes %d kW and generates %d FE/t");
        provider.add("tooltip.anvilcraft_pigsplus.menger_sponge_staff", "Remove a large amount of liquid");

        provider.add("config.jade.plugin_anvilcraft_pigsplus.electric_enchanting_table", "Electric Enchanting Table");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.enchanted_generator", "Enchanted Generator");
    }
}
