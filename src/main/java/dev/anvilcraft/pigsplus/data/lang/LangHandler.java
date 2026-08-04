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

        provider.add("screen.anvilcraft_pigsplus.grid_adapter.input", "Input: AnvilCraft Power -> FE");
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.output", "Output: FE -> AnvilCraft Power");
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.title", "Grid Adapter");
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.max", "MAX");
        provider.add("screen.anvilcraft_pigsplus.grid_adapter.value", "Conversion (kW): %s / %s");
        provider.add("tooltip.anvilcraft_pigsplus.grid_adapter.mode", "Mode: %s");
        provider.add("tooltip.anvilcraft_pigsplus.grid_adapter.use", "Right-click to configure. Shift + right-click an FE block to toggle it. Hold %s to open the mode wheel.");
        provider.add("tooltip.anvilcraft_pigsplus.grid_adapter.power", "Conversion: %s kW / max %s");
        provider.add("tooltip.anvilcraft_pigsplus.grid_adapter.configure", "Right-click to configure conversion");
        provider.add("message.anvilcraft_pigsplus.grid_adapter.no_energy_storage", "Target block has no FE energy storage");
        provider.add("message.anvilcraft_pigsplus.grid_adapter.no_receive", "Target block cannot receive FE");
        provider.add("message.anvilcraft_pigsplus.grid_adapter.no_extract", "Target block cannot provide FE");
        provider.add("message.anvilcraft_pigsplus.grid_adapter.enabled", "%s enabled on this block");
        provider.add("message.anvilcraft_pigsplus.grid_adapter.disabled", "Grid adapter disabled on this block");
        provider.add("message.anvilcraft_pigsplus.grid_adapter.switched", "Switched to %s on this block");
        provider.add("message.anvilcraft_pigsplus.grid_adapter.updated", "Conversion updated on this block");

        provider.add("message.anvilcraft_pigsplus.wireless_transmitter.placement_no_pos", "Transmission position not set");
        provider.add("message.anvilcraft_pigsplus.wireless_transmitter.target_too_far", "Transmission target too far");
        provider.add("message.anvilcraft_pigsplus.wireless_transmitter.target_not_loaded", "Transmission target is not loaded");
        provider.add("message.anvilcraft_pigsplus.wireless_transmitter.target_is_air", "Transmission target is invalid");
        provider.add("message.anvilcraft_pigsplus.wireless_transmitter.target_is_self", "Cannot use the transmitter itself as a target");
        provider.add("message.anvilcraft_pigsplus.wireless_transmitter.target_invalid", "Invalid transmission target");
        provider.add("item.anvilcraft_pigsplus.wireless_transmitter.pos_set", "Will transmit to %s");
        provider.add("tooltip.anvilcraft_pigsplus.wireless_transmitter.use", "Right-click a block to bind a target, then place it against an item or fluid container.");
        provider.add("tooltip.anvilcraft_pigsplus.wireless_transmitter.target", "Target: %s");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.wireless_transmitter", "Wireless Transmitter");

        provider.add("config.jade.plugin_anvilcraft_pigsplus.electric_enchanting_table", "Electric Enchanting Table");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.enchanted_generator", "Enchanted Generator");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.precision_magnetic_pivot", "Precision Magnetic Pivot");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.celestial_reformer", "Celestial Reformer");
        provider.add("config.jade.plugin_anvilcraft_pigsplus.brass_sink", "Brass Sink");
        provider.add("config.jade.plugin_anvilcraft.fluid_tank", "Fluid Tank");
        provider.add("screen.anvilcraft.cfa.megastructure.planetary_reformer", "Planetary Reformer");
        provider.add("screen.anvilcraft.cfa.megastructure.star_reformer", "Star Reformer");
        provider.add("screen.anvilcraft.cfa.megastructure.planetary_reformer.usage", "Consume resources to reform the celestial body.");
        provider.add("screen.anvilcraft.cfa.megastructure.star_reformer.usage", "Consume resources to reform the celestial body.");
        provider.add("screen.anvilcraft.cfa.class.special.fulgora", "Fulgora");
        provider.add("gui.anvilcraft_pigsplus.jei.fluid", "Fluid: %s: %s mB");
        provider.add("gui.anvilcraft_pigsplus.jei.item", "Item: %s x %s");
        provider.add("gui.anvilcraft_pigsplus.category.celestial_reformer", "Celestial Reformer");
        provider.add("gui.anvilcraft_pigsplus.jei.modification", "Modification: %s");
        provider.add("gui.anvilcraft_pigsplus.jei.laser", "Laser: level %s (%s)");
        provider.add("gui.anvilcraft_pigsplus.laser.type.any", "Any laser");
        provider.add("gui.anvilcraft_pigsplus.laser.type.gamma", "Gamma laser");
        provider.add("gui.anvilcraft_pigsplus.laser.type.normal", "Normal laser");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.item", "%s: %s / %s");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.fluid", "%s: %s / %s mB");
        provider.add("tooltip.anvilcraft_pigsplus.celestial_reformer.current.laser", "Laser: level %s (%s)");
        provider.add("enchantment.anvilcraft_pigsplus.endurance", "Endurance");
    }
}
