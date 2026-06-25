package dev.anvilcraft.pigsplus.data.lang;


import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;

public class JeiLang {
    @SuppressWarnings("checkstyle:LineLength")
    public static void init(RegistrumLangProvider provider) {
        provider.add(
            "jei.anvilcraft.pigsplus.info.spiritual_component",
            "When a Karakuri Component is destroyed, each enchantment provides a %.0f%% chance to generate at most one Medium Component."
        );

        provider.add(
            "jei.anvilcraft.pigsplus.info.ender_component",
            "Throw a Karakuri Component into the End portal, there is a %.0f%% chance to obtain a Ender Component."
        );

    }
}
