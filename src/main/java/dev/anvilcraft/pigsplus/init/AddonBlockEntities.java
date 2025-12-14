package dev.anvilcraft.pigsplus.init;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.anvilcraft.pigsplus.block.entity.AutoJewelCraftingTableBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.AutoRoyalGrindstoneBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.AutoRoyalSmithingTableBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.CauldronOutputBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.EnchantedGeneratorBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.AdjustablePowerConverterBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.SculkExtractorBlockEntity;
import dev.anvilcraft.pigsplus.client.renderer.blockentity.EnchantedGeneratorRenderer;
import dev.anvilcraft.pigsplus.client.renderer.blockentity.ElectricEnchantingTableRenderer;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

public class AddonBlockEntities {
    public static final BlockEntityEntry<AutoRoyalSmithingTableBlockEntity> AUTO_ROYAL_SMITHING_TABLE =
        REGISTRATE.blockEntity("auto_royal_smithing_table", AutoRoyalSmithingTableBlockEntity::new)
            .validBlock(AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK)
            .register();

    public static final BlockEntityEntry<AutoRoyalGrindstoneBlockEntity> AUTO_ROYAL_GRINDSTONE =
        REGISTRATE.blockEntity("auto_royal_grindstone", AutoRoyalGrindstoneBlockEntity::new)
            .validBlock(AddonBlocks.AUTO_ROYAL_GRINDSTONE_BLOCK)
            .register();

    public static final BlockEntityEntry<AutoJewelCraftingTableBlockEntity> AUTO_JEWEL_CRAFTING_TABLE =
        REGISTRATE.blockEntity("auto_jewel_crafting_table", AutoJewelCraftingTableBlockEntity::new)
            .validBlock(AddonBlocks.AUTO_JEWEL_CRAFTING_TABLE_BLOCK)
            .register();

    public static final BlockEntityEntry<CauldronOutputBlockEntity> CAULDRON_OUTPUT =
        REGISTRATE.blockEntity("cauldron_output", CauldronOutputBlockEntity::new)
            .validBlock(AddonBlocks.CAULDRON_OUTPUT)
            .register();

    public static final BlockEntityEntry<EnchantedGeneratorBlockEntity> ENCHANTMENT_GENERATOR =
        REGISTRATE.blockEntity("enchanted_generator", EnchantedGeneratorBlockEntity::createBlockEntity)
            .validBlock(AddonBlocks.ENCHANTMENT_GENERATOR_BLOCK)
            .renderer(() -> EnchantedGeneratorRenderer::new)
            .register();

    public static final BlockEntityEntry<ElectricEnchantingTableBlockEntity> ELECTRIC_ENCHANTING_TABLE =
        REGISTRATE.blockEntity("electric_enchanting_table", ElectricEnchantingTableBlockEntity::new)
            .validBlock(AddonBlocks.ELECTRIC_ENCHANTING_TABLE_BLOCK)
            .renderer(() -> ElectricEnchantingTableRenderer::new)
            .register();

    public static final BlockEntityEntry<SculkExtractorBlockEntity> SCULK_EXTRACTOR =
        REGISTRATE.blockEntity("sculk_extractor", SculkExtractorBlockEntity::new)
            .validBlock(AddonBlocks.SCULK_EXTRACTOR)
            .register();

    public static final BlockEntityEntry<AdjustablePowerConverterBlockEntity> ADJUSTABLE_POWER_CONVERTER =
        REGISTRATE.blockEntity("adjustable_power_converter", AdjustablePowerConverterBlockEntity::new)
            .validBlock(AddonBlocks.ADJUSTABLE_POWER_CONVERTER)
            .register();

    public static void register() {
    }
}