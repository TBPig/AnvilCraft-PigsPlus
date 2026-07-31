package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.block.handler.BrassSinkFluidHandler;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

public class CapabilitiesEventListener {
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlock(
            Capabilities.FluidHandler.BLOCK,
            (level, pos, state, blockEntity, side) -> BrassSinkFluidHandler.INSTANCE,
            AddonBlocks.BRASS_SINK.get()
        );

        List.of(
            AddonBlockEntities.ELECTRIC_ENCHANTING_TABLE.get(),
            AddonBlockEntities.AUTO_JEWEL_CRAFTING_TABLE.get(),
            AddonBlockEntities.AUTO_ROYAL_SMITHING_TABLE.get(),
            AddonBlockEntities.AUTO_ROYAL_GRINDSTONE.get()
        ).forEach(type -> event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                type,
                (be, side) -> be.getItemHandler()
            )
        );

        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            AddonBlockEntities.ADJUSTABLE_POWER_CONVERTER.get(),
            (be, side) -> be.getEnergyStorage()
        );
    }
}
