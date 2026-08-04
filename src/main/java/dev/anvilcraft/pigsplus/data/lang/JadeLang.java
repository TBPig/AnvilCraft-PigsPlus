package dev.anvilcraft.pigsplus.data.lang;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;

public class JadeLang {
    public static void init(RegistrumLangProvider provider) {
        provider.add("tooltip.anvilcraft_pigsplus.enchanted_generator.decrease_rate", "XP decrease rate: %s");
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
        provider.add("tooltip.anvilcraft_pigsplus.wireless_transmitter.target", "Target: %s");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.item", "%s: %s / %s");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.fluid", "%s: %s / %s mB");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.laser", "Laser: level %s (%s)");

        provider.add("config.jade.plugin_anvilcraft_pigsplus.wireless_transmitter", "Wireless Transmitter");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.electric_enchanting_table", "Electric Enchanting Table");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.enchanted_generator", "Enchanted Generator");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.precision_magnetic_pivot", "Precision Magnetic Pivot");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.celestial_reformer", "Celestial Reformer");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.brass_sink", "Brass Sink");
        provider.add("config.jade.plugin_anvilcraft.fluid_tank", "Fluid Tank");
    }
}
