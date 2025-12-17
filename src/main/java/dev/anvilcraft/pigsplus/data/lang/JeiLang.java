package dev.anvilcraft.pigsplus.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class JeiLang {
    @SuppressWarnings("checkstyle:LineLength")
    public static void init(RegistrateLangProvider provider) {
        provider.add(
            "jei.anvilcraft.pigsplus.info.spiritual_component",
            "When a Karakuri Component is destroyed, each enchantment provides a 20% chance to generate at most one Medium Component."
        );

        provider.add(
            "jei.anvilcraft.pigsplus.info.ender_component",
            "Throw a Karakuri Component into the End portal, there is a 20% chance to obtain a Ender Component."
        );


    }
}
