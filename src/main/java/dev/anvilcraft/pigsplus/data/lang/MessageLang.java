package dev.anvilcraft.pigsplus.data.lang;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumLangProvider;

public class MessageLang {
    public static void init(RegistrumLangProvider provider) {
        provider.add(
            "block.anvilcraft_pigsplus.enchanted_generator.placement_too_close_to_another",
            "Too close to another enchanted generator"
        );

        provider.add(
            "block.anvilcraft_pigsplus.memory_block_comparator.structure_too_large",
            "Saved structure is larger than 1x1x1, cannot load into memory block comparator"
        );

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
    }
}
