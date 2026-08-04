package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.WirelessTransmitterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum WirelessTransmitterJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof WirelessTransmitterBlockEntity entity) {
            BlockPos target = entity.getTargetPos();
            if (target != null) {
                tag.putLong("targetPos", target.asLong());
            }
        }
    }

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return true;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (serverData.contains("targetPos")) {
            BlockPos target = BlockPos.of(serverData.getLong("targetPos"));
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.wireless_transmitter.target",
                target.toShortString()
            ));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("wireless_transmitter");
    }
}
