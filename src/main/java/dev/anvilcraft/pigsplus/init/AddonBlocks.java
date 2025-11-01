package dev.anvilcraft.pigsplus.init;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.anvilcraft.pigsplus.block.ChainSmithingTableBlock;
import dev.anvilcraft.pigsplus.block.WeakResinBlock;
import dev.anvilcraft.pigsplus.block.item.WeakResinBlockItem;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
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
                .define('S', ModBlocks.RESIN_BLOCK.get())
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
        .register();

    public static void register() {
    }
}