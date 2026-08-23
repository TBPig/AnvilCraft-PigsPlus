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
        AdvancementLang.init(provider);
        JeiLang.init(provider);
        RecipeLang.init(provider);
        JadeLang.init(provider);
        MessageLang.init(provider);
        ItemLang.init(provider);
        ScreenLang.init(provider);

        provider.add("gui.anvilcraft_pigsplus.jei.fluid", "Fluid: %s: %s mB");
        provider.add("gui.anvilcraft_pigsplus.jei.item", "Item: %s x %s");
        provider.add("gui.anvilcraft_pigsplus.category.celestial_reformer", "Celestial Reformer");
        provider.add("gui.anvilcraft_pigsplus.jei.modification", "Modification: %s");
        provider.add("gui.anvilcraft_pigsplus.jei.laser", "Laser: level %s (%s)");
        provider.add("gui.anvilcraft_pigsplus.laser.type.any", "Any laser");
        provider.add("gui.anvilcraft_pigsplus.laser.type.gamma", "Gamma laser");
        provider.add("gui.anvilcraft_pigsplus.laser.type.normal", "Normal laser");
        provider.add("enchantment.anvilcraft_pigsplus.endurance", "Endurance");
        provider.add("itemGroup.anvilcraft_pigsplus.section.tools", "Tools");
        provider.add("itemGroup.anvilcraft_pigsplus.section.fe_compatibility", "FE Compatibility");
        provider.add("itemGroup.anvilcraft_pigsplus.section.redstone", "Redstone");
        provider.add("itemGroup.anvilcraft_pigsplus.section.automation", "Automation");
        provider.add("itemGroup.anvilcraft_pigsplus.section.materials", "Materials");
    }
}
