package dev.anvilcraft.pigsplus.init;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.anvilcraft.pigsplus.block.AutoRoyalSmithingTableBlock;
import dev.anvilcraft.pigsplus.block.ChainSmithingTableBlock;
import dev.anvilcraft.pigsplus.block.ElectricEnchantingTableBlock;
import dev.anvilcraft.pigsplus.block.EnchantedGeneratorBlock;
import dev.anvilcraft.pigsplus.block.WeakResinBlock;
import dev.anvilcraft.pigsplus.block.item.WeakResinBlockItem;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.util.DataGenUtil;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.Tags;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

public class AddonBlocks {
    static {
        REGISTRATE.defaultCreativeTab(AddonItemGroups.ADDON_ITEMS.getKey());
    }

    public static final BlockEntry<? extends Block> WEAK_RESIN_BLOCK = REGISTRATE
        .block("weak_resin_block", WeakResinBlock::new)
        .lang("Block of Weak Resin")
        .initialProperties(() -> Blocks.SLIME_BLOCK)
        .blockstate(DataGenUtil::noExtraModelOrState)
        .properties(properties -> properties.sound(SoundType.HONEY_BLOCK))
        .item(WeakResinBlockItem::new)
        .build()
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("SSS")
            .pattern("SES")
            .pattern("SSS")
            .define('S', ModItems.RESIN)
            .define('E', Items.FERMENTED_SPIDER_EYE)
            .unlockedBy(AnvilCraftDatagen.hasItem(ModBlocks.RESIN_BLOCK), AnvilCraftDatagen.has(ModBlocks.RESIN_BLOCK))
            .save(provider))
        .register();

    public static final BlockEntry<ChainSmithingTableBlock> CHAIN_SMITHING_TABLE_BLOCK = REGISTRATE
        .block("chain_smithing_table", ChainSmithingTableBlock::new)
        .lang("Chain Smithing Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.strength(5.0f, 1200f))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("   ")
            .pattern("SCS")
            .pattern("KKK")
            .define('S', Blocks.SMITHING_TABLE)
            .define('C', ModBlocks.ROYAL_SMITHING_TABLE)
            .define('K', AddonItems.KARAKURI_COMPONENT)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(ModBlocks.ROYAL_SMITHING_TABLE),
                AnvilCraftDatagen.has(ModBlocks.ROYAL_SMITHING_TABLE)
            )
            .save(provider))
        .register();

    public static final BlockEntry<AutoRoyalSmithingTableBlock> AUTO_ROYAL_SMITHING_TABLE_BLOCK = REGISTRATE
        .block("auto_royal_smithing_table", AutoRoyalSmithingTableBlock::new)
        .lang("Auto Royal Smithing Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.strength(5.0f, 1200f))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern(" K ")
            .pattern(" S ")
            .pattern(" M ")
            .define('K', AddonItems.KARAKURI_COMPONENT)
            .define('S', ModBlocks.ROYAL_SMITHING_TABLE)
            .define('M', ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(ModBlocks.ROYAL_SMITHING_TABLE),
                AnvilCraftDatagen.has(ModBlocks.ROYAL_SMITHING_TABLE)
            )
            .save(provider))
        .register();

    public static final BlockEntry<Block> CHAOTIC_RAW_ORE_BLOCK = REGISTRATE
        .block("chaotic_raw_ore_block", Block::new)
        .lang("Chaotic Raw Ore Block")
        .initialProperties(() -> Blocks.RAW_IRON_BLOCK)
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("XXX")
            .pattern("XXX")
            .pattern("XXX")
            .define('X', AddonItems.CHAOTIC_RAW_ORE)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.CHAOTIC_RAW_ORE),
                AnvilCraftDatagen.has(AddonItems.CHAOTIC_RAW_ORE)
            )
            .save(provider))
        .item()
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.STORAGE_BLOCKS)
        .register();

    public static final BlockEntry<Block> DEEPSLATE_CHAOTIC_ORE = REGISTRATE
        .block("deepslate_chaotic_ore", Block::new)
        .initialProperties(() -> Blocks.DEEPSLATE_IRON_ORE)
        .item()
        .tag(Tags.Items.ORES)
        .build()
        .loot((tables, block) -> tables.add(block, tables.createOreDrop(block, AddonItems.CHAOTIC_RAW_ORE.get())))
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.ORES, Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
        .register();

    public static final BlockEntry<EnchantedGeneratorBlock> ENCHANTMENT_GENERATOR_BLOCK = REGISTRATE
        .block("enchanted_generator", EnchantedGeneratorBlock::new)
        .lang("Enchanted Generator")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.strength(5.0f, 1200f))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .simpleItem()
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
            .pattern("AAA")
            .pattern("ABA")
            .pattern("CCC")
            .define('A', AddonItems.SPIRITUAL_COMPONENT)
            .define('B', ModBlocks.CHARGE_COLLECTOR)
            .define('C', ModItems.FROST_METAL_INGOT)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.SPIRITUAL_COMPONENT),
                AnvilCraftDatagen.has(AddonItems.SPIRITUAL_COMPONENT)
            )
            .save(provider))
        .register();

    public static final BlockEntry<ElectricEnchantingTableBlock> ELECTRIC_ENCHANTING_TABLE_BLOCK = REGISTRATE
        .block("electric_enchanting_table", ElectricEnchantingTableBlock::new)
        .lang("Electric Enchanting Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.noOcclusion().isValidSpawn(Blocks::never))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .properties(properties -> properties.sound(SoundType.WOOD))
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
            .pattern("ADA")
            .pattern("AEA")
            .pattern("CBC")
            .define('A', AddonItems.SPIRITUAL_COMPONENT)
            .define('B', ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK)
            .define('C', ModBlocks.TRANSCENDENCE_ANVIL)
            .define('D', Tags.Items.GLASS_PANES)
            .define('E', Blocks.ENCHANTING_TABLE)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.SPIRITUAL_COMPONENT), AnvilCraftDatagen.has(AddonItems.SPIRITUAL_COMPONENT))
            .save(provider)
        )
        .register();

    public static void register() {
    }
}