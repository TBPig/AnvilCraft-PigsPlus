package dev.anvilcraft.pigsplus.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.anvilcraft.pigsplus.AddonConfig;
import dev.anvilcraft.lib.config.ConfigData;

public class LangHandler {

    /**
     * language file init
     *
     * @param provider provider
     */
    public static void init(RegistrateLangProvider provider) {
        ConfigData.readConfigClass(provider, AddonConfig.class);

        provider.add("block.anvilcraft_pigsplus.enchanted_generator.placement_too_close_to_another", "Too close to another enchanted generator");

        provider.add("tooltip.anvilcraft.pigsplus.enchanted_generator.time", "Remaining time: %s");
        provider.add("tooltip.anvilcraft.pigsplus.enchanted_generator.max_power_value", "Max power consumption: %d kW");
        provider.add("tooltip.anvilcraft.pigsplus.enchanted_generator.power_rate", "Power consumption rate: %s");
    }
}
