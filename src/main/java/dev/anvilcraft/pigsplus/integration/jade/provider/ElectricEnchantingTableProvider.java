package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;

public enum ElectricEnchantingTableProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;
    private static final BoxStyle.GradientBorder STYLE = BoxStyle.GradientBorder.TRANSPARENT.clone();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (serverData.contains("decreaseRate")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.decrease_rate",
                String.format("%.3f", serverData.getDouble("decreaseRate"))
            ));
        }
        if (serverData.contains("needXpLiquid") && serverData.contains("absorbedXpLiquid")) {
            int consume = serverData.getInt("absorbedXpLiquid");
            int generate = serverData.getInt("needXpLiquid");
            if (consume > 0) {
                tooltip.add(Component.translatable(consume + "/" + generate + " mB"));
            }
        }

    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof ElectricEnchantingTableBlockEntity entity) {
            tag.putDouble("decreaseRate", entity.getDecreaseRate());
            tag.putInt("needXpLiquid", entity.getNeedXpLiquid());
            tag.putInt("absorbedXpLiquid", entity.getAbsorbedXpLiquid());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("electric_enchanting_table");
    }
}
