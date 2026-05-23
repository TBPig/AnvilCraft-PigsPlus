package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.init.block.ModBlocks;

public class RegistrumBlockRecipeLoader {
    public static <T extends Block> void pigAnvil(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.MISC, ctx.get())
            .pattern("AAA")
            .pattern(" B ")
            .pattern("BBB")
            .define('A', Items.STRIPPED_CHERRY_WOOD)
            .define('B', Items.CHERRY_PLANKS)
            .unlockedBy(AnvilCraftDatagen.hasItem(Items.STRIPPED_CHERRY_WOOD), AnvilCraftDatagen.has(lookup, Items.STRIPPED_CHERRY_WOOD))
            .save(provider);
    }

    public static <T extends Block> void weakResinBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("SSS")
            .pattern("SES")
            .pattern("SSS")
            .define('S', ModItems.RESIN)
            .define('E', Items.FERMENTED_SPIDER_EYE)
            .unlockedBy(AnvilCraftDatagen.hasItem(ModBlocks.RESIN_BLOCK), AnvilCraftDatagen.has(lookup, ModBlocks.RESIN_BLOCK))
            .save(provider);
    }

    public static <T extends Block> void cauldronOutput(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.MISC, ctx.get(), 4)
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .define('A', Items.IRON_INGOT)
            .define('B', AddonItems.KARAKURI_COMPONENT)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT), AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT))
            .save(provider);
    }

    public static <T extends Block> void redstoneConduitBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.REDSTONE, ctx.get(), 32)
            .pattern("HKH")
            .pattern("KSK")
            .pattern("HKH")
            .define('S', AddonItems.KARAKURI_COMPONENT)
            .define('K', Items.REDSTONE)
            .define('H', ModItemTags.BRASS_INGOTS)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT), AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT))
            .save(provider);
    }

    public static <T extends Block> void blockBreaker(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("AA ")
            .pattern("DCB")
            .pattern("AA ")
            .define('A', Items.COBBLESTONE)
            .define('B', Items.STONECUTTER)
            .define('C', AddonItems.KARAKURI_COMPONENT)
            .define('D', Items.HOPPER)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT), AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT))
            .save(provider);
    }

    public static <T extends Block> void chainSmithingTableBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("   ")
            .pattern("SCS")
            .pattern("KKK")
            .define('S', Blocks.SMITHING_TABLE)
            .define('C', ModBlocks.ROYAL_SMITHING_TABLE)
            .define('K', AddonItems.KARAKURI_COMPONENT)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(ModBlocks.ROYAL_SMITHING_TABLE),
                AnvilCraftDatagen.has(lookup, ModBlocks.ROYAL_SMITHING_TABLE)
            )
            .save(provider);
    }

    public static <T extends Block> void autoJewelCraftingTableBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("GKG")
            .pattern("GJG")
            .pattern("IMI")
            .define('K', AddonItems.KARAKURI_COMPONENT)
            .define('J', ModBlocks.JEWEL_CRAFTING_TABLE)
            .define('M', ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK)
            .define('G', Blocks.GLASS)
            .define('I', Items.IRON_INGOT)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(ModBlocks.JEWEL_CRAFTING_TABLE),
                AnvilCraftDatagen.has(lookup, ModBlocks.JEWEL_CRAFTING_TABLE)
            )
            .save(provider);
    }

    public static <T extends Block> void autoRoyalSmithingTableBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("GKG")
            .pattern("GRG")
            .pattern("IMI")
            .define('K', AddonItems.KARAKURI_COMPONENT)
            .define('R', ModBlocks.ROYAL_SMITHING_TABLE)
            .define('M', ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK)
            .define('G', Blocks.GLASS)
            .define('I', Items.IRON_INGOT)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(ModBlocks.ROYAL_SMITHING_TABLE),
                AnvilCraftDatagen.has(lookup, ModBlocks.ROYAL_SMITHING_TABLE)
            )
            .save(provider);
    }

    public static <T extends Block> void autoRoyalGrindstoneBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("GKG")
            .pattern("GRG")
            .pattern("IMI")
            .define('K', AddonItems.KARAKURI_COMPONENT)
            .define('R', ModBlocks.ROYAL_GRINDSTONE)
            .define('M', ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK)
            .define('G', Blocks.GLASS)
            .define('I', Items.IRON_INGOT)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(ModBlocks.ROYAL_GRINDSTONE),
                AnvilCraftDatagen.has(lookup, ModBlocks.ROYAL_GRINDSTONE)
            )
            .save(provider);
    }

    public static <T extends Block> void adjustablePowerConverter(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.MISC, ctx.get())
            .pattern("ABA")
            .pattern("BCB")
            .pattern("ABA")
            .define('A', Items.COPPER_INGOT)
            .define('B', ModBlocks.POWER_CONVERTER_BIG)
            .define('C', AddonItems.KARAKURI_COMPONENT)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT), AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT))
            .save(provider);
    }

    public static <T extends Block> void chaoticRawOreBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, ctx.get())
            .pattern("XXX")
            .pattern("XXX")
            .pattern("XXX")
            .define('X', AddonItems.CHAOTIC_RAW_ORE)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.CHAOTIC_RAW_ORE),
                AnvilCraftDatagen.has(lookup, AddonItems.CHAOTIC_RAW_ORE)
            )
            .save(provider);
    }

    public static <T extends Block> void enchantmentGeneratorBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.MISC, ctx.get())
            .pattern("ADA")
            .pattern("DBD")
            .pattern("CCC")
            .define('A', AddonItems.SPIRITUAL_COMPONENT)
            .define('B', ModBlocks.CHARGE_COLLECTOR)
            .define('C', ModItems.FROST_METAL_INGOT)
            .define('D', ModItemTags.SILVER_PLATES)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.SPIRITUAL_COMPONENT),
                AnvilCraftDatagen.has(lookup, AddonItems.SPIRITUAL_COMPONENT)
            )
            .save(provider);
    }

    public static <T extends Block> void electricEnchantingTableBlock(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.MISC, ctx.get())
            .pattern("ADA")
            .pattern("AEA")
            .pattern("CBC")
            .define('A', AddonItems.SPIRITUAL_COMPONENT)
            .define('B', ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK)
            .define('C', ModBlocks.TRANSCENDENCE_ANVIL)
            .define('D', Tags.Items.GLASS_PANES)
            .define('E', Blocks.ENCHANTING_TABLE)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.SPIRITUAL_COMPONENT), AnvilCraftDatagen.has(lookup, AddonItems.SPIRITUAL_COMPONENT))
            .save(provider);
    }

    public static <T extends Block> void sculkExtractor(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.MISC, ctx.get())
            .pattern(" S ")
            .pattern("LCL")
            .pattern("LCL")
            .define('S', Blocks.SCULK_CATALYST)
            .define('L', Items.ECHO_SHARD)
            .define('C', AddonItems.SPIRITUAL_COMPONENT)
            .unlockedBy(AnvilCraftDatagen.hasItem(Items.ECHO_SHARD), AnvilCraftDatagen.has(lookup, Items.ECHO_SHARD))
            .save(provider);
    }

    public static <T extends Block> void voidCatalyst(DataGenContext<Block, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.MISC, ctx.get())
            .pattern(" A ")
            .pattern("ABA")
            .pattern(" A ")
            .define('A', ModBlocks.VOID_MATTER_BLOCK)
            .define('B', AddonItems.KARAKURI_COMPONENT)
            .unlockedBy(AnvilCraftDatagen.hasItem(ModBlocks.VOID_MATTER_BLOCK), AnvilCraftDatagen.has(lookup, ModBlocks.VOID_MATTER_BLOCK))
            .save(provider);
    }
}
