package dev.anvilcraft.pigsplus.init;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.anvilcraft.pigsplus.block.ChainSmithingTableBlock;
import dev.anvilcraft.pigsplus.block.AutoRoyalSmithingTableBlock;
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
        .recipe((ctx, provider) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ctx.get())
                .pattern("SSS")
                .pattern("SES")
                .pattern("SSS")
                .define('S', ModItems.RESIN)
                .define('E', Items.FERMENTED_SPIDER_EYE)
                .unlockedBy(AnvilCraftDatagen.hasItem(ModBlocks.RESIN_BLOCK), AnvilCraftDatagen.has(ModBlocks.RESIN_BLOCK))
                .save(provider);
        })
        .register();

    public static final BlockEntry<ChainSmithingTableBlock> CHAIN_SMITHING_TABLE_BLOCK = REGISTRATE
        .block("chain_smithing_table", ChainSmithingTableBlock::new)
        .lang("Chain Smithing Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.strength(5.0f, 1200f))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
        .recipe((ctx, provider) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ctx.get())
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
                .save(provider);
        })
        .register();

    public static final BlockEntry<AutoRoyalSmithingTableBlock> AUTO_ROYAL_SMITHING_TABLE_BLOCK = REGISTRATE
        .block("auto_royal_smithing_table", AutoRoyalSmithingTableBlock::new)
        .lang("Auto Royal Smithing Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.strength(5.0f, 1200f))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
        .recipe((ctx, provider) -> {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ctx.get())
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
                .save(provider);
        })
        .register();

    public static void register() {
    }
}