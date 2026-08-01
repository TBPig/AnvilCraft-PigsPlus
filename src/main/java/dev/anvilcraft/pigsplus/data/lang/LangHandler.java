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
        RecipeLang.init(provider);

        provider.add(
            "block.anvilcraft_pigsplus.enchanted_generator.placement_too_close_to_another",
            "Too close to another enchanted generator"
        );

        provider.add(
            "block.anvilcraft_pigsplus.memory_block_comparator.structure_too_large",
            "Saved structure is larger than 1x1x1, cannot load into memory block comparator"
        );

        provider.add("tooltip.anvilcraft_pigsplus.enchanted_generator.decrease_rate", "XP decrease rate: %s");
        provider.add("tooltip.anvilcraft_pigsplus.menger_sponge_staff", "Remove a large amount of liquid");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.time", "Status: %s");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.time.working", "Working");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.time.stopped", "Stopped");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.friction_count", "Friction Count: %s");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.clockwise", "Rotation: %s");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.clockwise.clockwise", "Clockwise");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.clockwise.counter_clockwise", "Counter-clockwise");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.clockwise.none", "None");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.pre_direction", "Previous Direction: %s");
        provider.add("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.pre_direction.none", "None");

        provider.add("tooltip.anvilcraft_pigsplus.portable_wireless_charger", "When backpack, it consumes %d kW and generates %d FE/t");

        provider.add("config.jade.plugin_anvilcraft_pigsplus.electric_enchanting_table", "Electric Enchanting Table");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.enchanted_generator", "Enchanted Generator");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.precision_magnetic_pivot", "Precision Magnetic Pivot");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.celestial_reformer", "Celestial Reformer");
        provider.add("screen.anvilcraft.cfa.megastructure.planetary_reformer", "Planetary Reformer");
        provider.add("screen.anvilcraft.cfa.megastructure.star_reformer", "Star Reformer");
        provider.add(
            "screen.anvilcraft.cfa.megastructure.star_reformer.usage",
            "Build with 64 Celestial Reformer Components, then supply star reform materials to logistics interfaces."
        );
        provider.add("gui.anvilcraft_pigsplus.jei.fluid", "Fluid: %s: %s mB");
        provider.add("gui.anvilcraft_pigsplus.category.celestial_reformer", "Celestial Reformer");
        provider.add("gui.anvilcraft_pigsplus.jei.modification", "Modification: %s");
        provider.add("gui.anvilcraft_pigsplus.jei.laser", "Laser: level %s (%s)");
        provider.add("gui.anvilcraft_pigsplus.laser.type.any", "Any laser");
        provider.add("gui.anvilcraft_pigsplus.laser.type.gamma", "Gamma laser");
        provider.add("gui.anvilcraft_pigsplus.laser.type.normal", "Normal laser");
        provider.add(
            "screen.anvilcraft.cfa.megastructure.planetary_reformer.usage",
            "Build with 16 Celestial Reformer Components, then supply materials to logistics interfaces to automatically select rotation or magnetic field reform."
        );
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.progress", "Reforming Progress: %s");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current", "Current: %s");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.item", "%s: %s / %s");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.fluid", "%s: %s / %s mB");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.laser", "Laser: level %s (%s)");
        provider.add("enchantment.anvilcraft_pigsplus.endurance", "Endurance");
    }
}
