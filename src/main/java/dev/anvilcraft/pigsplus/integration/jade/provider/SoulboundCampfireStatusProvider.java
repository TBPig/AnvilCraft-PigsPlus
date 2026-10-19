package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.SoulboundCampfireBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum SoulboundCampfireStatusProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof SoulboundCampfireBlockEntity campfire) {
            tag.putBoolean("working", campfire.isWorking());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        tooltip.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.working_status",
            Component.translatable(accessor.getServerData().getBoolean("working")
                ? "tooltip.anvilcraft_pigsplus.working_status.working"
                : "tooltip.anvilcraft_pigsplus.working_status.idle")
        ));
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("soulbound_campfire");
    }
}
