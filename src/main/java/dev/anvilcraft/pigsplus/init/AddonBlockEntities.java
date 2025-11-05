package dev.anvilcraft.pigsplus.init;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.anvilcraft.pigsplus.block.entity.AutoRoyalSmithingTableBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.EnchantedGeneratorBlockEntity;
import dev.anvilcraft.pigsplus.client.renderer.blockentity.EnchantedGeneratorRenderer;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

public class AddonBlockEntities {
    public static final BlockEntityEntry<AutoRoyalSmithingTableBlockEntity> AUTO_ROYAL_SMITHING_TABLE =
        REGISTRATE.blockEntity("auto_royal_smithing_table", AutoRoyalSmithingTableBlockEntity::new)
            .validBlock(AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK)
            .register();

    public static final BlockEntityEntry<EnchantedGeneratorBlockEntity> ENCHANTMENT_COLLECTOR =
        REGISTRATE.blockEntity("enchanted_generator", EnchantedGeneratorBlockEntity::createBlockEntity)
            .validBlock(AddonBlocks.ENCHANTMENT_COLLECTOR_BLOCK)
            .renderer(() -> EnchantedGeneratorRenderer::new)
            .register();


    public static void register() {
    }
}