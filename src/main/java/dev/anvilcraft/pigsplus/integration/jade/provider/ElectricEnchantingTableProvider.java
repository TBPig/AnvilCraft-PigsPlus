package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import dev.dubhe.anvilcraft.util.FormattingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.CONFIG;

public enum ElectricEnchantingTableProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (serverData.contains("time")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.time",
                FormattingUtil.toFormattedTime(serverData.getInt("time"), 5)));
        }
        if (serverData.contains("maxPowerValue")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.max_power_value",
                serverData.getInt("maxPowerValue")));
        }
        if (serverData.contains("powerRate")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.power_rate",
                String.format("%.3f", serverData.getDouble("powerRate"))));
        }
        if (serverData.contains("prevPowerValue")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.previous_energy_consumption",
                serverData.getInt("prevPowerValue")));
        }

    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof ElectricEnchantingTableBlockEntity entity) {
            tag.putInt("time", entity.getTime());
            tag.putInt("maxPowerValue", CONFIG.electricEnchantingTable.basePowerConsumptionLimit);
            tag.putDouble("powerRate", entity.getPowerRate());
            tag.putInt("prevPowerValue", entity.getPrevPowerValue());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("electric_enchanting_table");
    }
}
