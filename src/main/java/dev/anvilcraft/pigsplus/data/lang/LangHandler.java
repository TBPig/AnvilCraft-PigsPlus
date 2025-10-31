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
    }
}
