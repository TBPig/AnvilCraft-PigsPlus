package dev.anvilcraft.pigsplus.integration.jade.provider.client;

import dev.dubhe.anvilcraft.util.FormattingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static dev.anvilcraft.pigsplus.integration.jade.provider.ElectricEnchantingTableProvider.UID;

public enum ElectricEnchantingTableClientProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (serverData.contains("time")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.time",
                FormattingUtil.toFormattedTime(serverData.getIntOr("time", 0), 5)
            ));
        }
        if (serverData.contains("maxPowerValue")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.max_power_value",
                serverData.getIntOr("maxPowerValue", 0)
            ));
        }
        if (serverData.contains("powerRate")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.power_rate",
                String.format("%.3f", serverData.getDoubleOr("powerRate", 0.0))
            ));
        }
        if (serverData.contains("prevPowerValue")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.previous_energy_consumption",
                serverData.getIntOr("prevPowerValue", 0)
            ));
        }

    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
