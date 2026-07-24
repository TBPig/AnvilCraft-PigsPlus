package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import static dev.dubhe.anvilcraft.data.recipe.util.RecipeLoaderUtil.getName;

public class VanillaRecipesLoader {
    public static void init(RegistrumRecipeProvider provider) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_BOARD, 16)
            .pattern("W")
            .pattern("R")
            .define('W', AddonItems.KARAKURI_COMPONENT)
            .define('R', ModItems.HARDEND_RESIN)
            .unlockedBy("hasitem", AnvilCraftDatagen.has(AddonItems.KARAKURI_COMPONENT))
            .save(provider, AnvilCraftPigsPlus.of("circuit_board"));

        cuttingUniversalRedstoneComponent(provider, AddonBlocks.MEMORY_BLOCK_COMPARATOR, 1);
        cuttingUniversalRedstoneComponent(provider, AddonBlocks.REDSTONE_CONDUIT_BLOCK, 64);
        cuttingUniversalRedstoneComponent(provider, ModBlocks.PULSE_GENERATOR, 1);
        cuttingUniversalRedstoneComponent(provider, ModBlocks.ADVANCED_COMPARATOR, 1);
        cuttingUniversalRedstoneComponent(provider, ModBlocks.BLOCK_COMPARATOR, 1);
        cuttingUniversalRedstoneComponent(provider, ModBlocks.ITEM_DETECTOR, 1);
        cuttingUniversalRedstoneComponent(provider, ModBlocks.REDSTONE_WIRE, 32);
        cuttingUniversalRedstoneComponent(provider, Items.REPEATER, 4);
        cuttingUniversalRedstoneComponent(provider, Items.COMPARATOR, 3);
        cuttingUniversalRedstoneComponent(provider, Items.LEVER, 8);
        cuttingUniversalRedstoneComponent(provider, Items.TRIPWIRE_HOOK, 4);
        cuttingUniversalRedstoneComponent(provider, Items.OBSERVER, 4);
    }


    public static void cuttingUniversalRedstoneComponent(RegistrumRecipeProvider provider, ItemLike result, int n) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(AddonItems.UNIVERSAL_REDSTONE_COMPONENT), RecipeCategory.REDSTONE, result, n)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.UNIVERSAL_REDSTONE_COMPONENT),
                AnvilCraftDatagen.has(AddonItems.UNIVERSAL_REDSTONE_COMPONENT)
            )
            .save(provider, AnvilCraftPigsPlus.of("stonecutting/%s".formatted(getName(result))));
    }
}
