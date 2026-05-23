package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class CapabilitiesEventListener {
    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        List.of(
            AddonBlockEntities.ELECTRIC_ENCHANTING_TABLE.get(),
            AddonBlockEntities.AUTO_JEWEL_CRAFTING_TABLE.get(),
            AddonBlockEntities.AUTO_ROYAL_SMITHING_TABLE.get(),
            AddonBlockEntities.AUTO_ROYAL_GRINDSTONE.get()
        ).forEach(type -> event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                type,
                (be, side) -> be.getItemHandler()
            )
        );

        event.registerBlockEntity(
            Capabilities.Item.BLOCK,
            AddonBlockEntities.ADJUSTABLE_POWER_CONVERTER.get(),
            (be, side) -> be.getEnergyStorage()
        );
    }
}
