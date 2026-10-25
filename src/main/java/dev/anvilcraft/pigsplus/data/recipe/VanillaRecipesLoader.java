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
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
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

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, AddonItems.LOGIC_ALLOY_INGOT, 9)
            .requires(AddonBlocks.LOGIC_ALLOY_BLOCK)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonBlocks.LOGIC_ALLOY_BLOCK),
                AnvilCraftDatagen.has(AddonBlocks.LOGIC_ALLOY_BLOCK)
            )
            .save(provider, AnvilCraftPigsPlus.of("logic_alloy_ingot_from_block"));

        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, AddonBlocks.MEMORY_BLOCK_COMPARATOR, 1);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, AddonBlocks.REDSTONE_CONDUIT_BLOCK, 64);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, ModBlocks.PULSE_GENERATOR, 1);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, ModBlocks.ADVANCED_COMPARATOR, 1);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, ModBlocks.BLOCK_COMPARATOR, 1);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, ModBlocks.ITEM_DETECTOR, 1);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, ModBlocks.REDSTONE_DICE, 2);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, ModBlocks.BIG_RED_BUTTON, 8);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, ModBlocks.REDSTONE_WIRE, 32);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, Items.REPEATER, 4);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, Items.COMPARATOR, 3);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, Items.LEVER, 8);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, Items.TRIPWIRE_HOOK, 4);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.UNIVERSAL_REDSTONE_COMPONENT, Items.OBSERVER, 4);

        cutting(provider, RecipeCategory.REDSTONE, AddonItems.LOGIC_ALLOY_INGOT, ModItems.CIRCUIT_BOARD, 8);
        cutting(provider, RecipeCategory.REDSTONE, AddonItems.LOGIC_ALLOY_INGOT, ModItems.PROCESSOR, 4);
        SingleItemRecipeBuilder.stonecutting(
                Ingredient.of(AddonItems.LOGIC_ALLOY_NUGGET),
                RecipeCategory.REDSTONE,
                ModBlocks.REDSTONE_WIRE,
                32
            )
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.LOGIC_ALLOY_NUGGET), AnvilCraftDatagen.has(AddonItems.LOGIC_ALLOY_NUGGET))
            .save(provider, AnvilCraftPigsPlus.of("stonecutting/redstone_wire_2"));
    }


    private static void cutting(RegistrumRecipeProvider provider, RecipeCategory category, ItemLike resource, ItemLike result, int n) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(resource), category, result, n)
            .unlockedBy(AnvilCraftDatagen.hasItem(resource), AnvilCraftDatagen.has(resource))
            .save(provider, AnvilCraftPigsPlus.of("stonecutting/%s".formatted(getName(result))));
    }
}
