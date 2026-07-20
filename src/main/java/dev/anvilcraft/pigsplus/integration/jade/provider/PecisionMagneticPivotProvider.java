package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.PecisionMagneticPivotBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum PecisionMagneticPivotProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();

        int time = serverData.getInt("time");
        if (time > 0) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.time",
                Component.translatable("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.time.working")
            ));
        } else {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.time",
                Component.translatable("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.time.stopped")
            ));
        }

        tooltip.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.friction_count",
            serverData.getInt("frictionCount")
        ));

        int clockwise = serverData.getInt("clockwise");
        String clockKey;
        if (clockwise == 1) {
            clockKey = "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.clockwise.clockwise";
        } else if (clockwise == -1) {
            clockKey = "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.clockwise.counter_clockwise";
        } else {
            clockKey = "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.clockwise.none";
        }
        tooltip.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.clockwise",
            Component.translatable(clockKey)
        ));

        if (serverData.contains("preDirection")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.pre_direction",
                serverData.getString("preDirection")
            ));
        } else {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.pre_direction",
                Component.translatable("tooltip.anvilcraft_pigsplus.precision_magnetic_pivot.pre_direction.none")
            ));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof PecisionMagneticPivotBlockEntity entity) {
            tag.putInt("time", entity.getTime());
            tag.putInt("frictionCount", entity.getFrictionCount());
            tag.putInt("clockwise", entity.getClockwise());
            if (entity.getPreDirection() != null) {
                tag.putString("preDirection", entity.getPreDirection().getName());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("precision_magnetic_pivot");
    }
}
