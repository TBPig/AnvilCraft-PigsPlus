package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.BeamHeatConverterBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum BeamHeatConverterProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof BeamHeatConverterBlockEntity entity) {
            tag.putInt("cycleIndex", entity.getCycleIndex());
            tag.putInt("laserLevel", entity.getLaserLevel());
            tag.putBoolean("working", entity.isWorking());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        int cycleIndex = serverData.getInt("cycleIndex");
        if (cycleIndex <= 0) {
            return;
        }
        tooltip.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.beam_heat_converter.cycle",
            cycleIndex
        ));
        tooltip.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.beam_heat_converter.laser",
            serverData.getInt("laserLevel")
        ));
        tooltip.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.working_status",
            Component.translatable(serverData.getBoolean("working")
                ? "tooltip.anvilcraft_pigsplus.working_status.working"
                : "tooltip.anvilcraft_pigsplus.working_status.idle")
        ));
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("beam_heat_converter");
    }
}
