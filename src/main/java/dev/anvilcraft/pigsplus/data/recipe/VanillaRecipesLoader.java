package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class VanillaRecipesLoader {
    public static void init(RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapelessRecipeBuilder.shapeless(lookup, RecipeCategory.MISC, ModItems.CIRCUIT_BOARD, 16)
            .requires(ModItems.HARDEND_RESIN)
            .requires(ModItems.HARDEND_RESIN)
            .requires(AddonItems.KARAKURI_COMPONENT)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT),
                AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT)
            )
            .save(provider, ResourceKey.create(Registries.RECIPE, AnvilCraftPigsPlus.of("circuit_board")));

        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, Items.REPEATER, 8)
            .pattern("   ")
            .pattern("TRT")
            .pattern("BBB")
            .define('R', AddonItems.KARAKURI_COMPONENT)
            .define('T', Items.REDSTONE_TORCH)
            .define('B', ModItems.HARDEND_RESIN)
            .unlockedBy(AnvilCraftDatagen.hasItem(ModItems.HARDEND_RESIN), AnvilCraftDatagen.has(lookup, ModItems.HARDEND_RESIN))
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT),
                AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT)
            )
            .save(provider, AnvilCraftPigsPlus.of("repeater").toString());

        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.BUILDING_BLOCKS, Items.COMPARATOR, 8)
            .pattern(" T ")
            .pattern("TQT")
            .pattern("BBB")
            .define('Q', AddonItems.KARAKURI_COMPONENT)
            .define('T', Items.REDSTONE_TORCH)
            .define('B', ModItems.HARDEND_RESIN)
            .unlockedBy(AnvilCraftDatagen.hasItem(ModItems.HARDEND_RESIN), AnvilCraftDatagen.has(lookup, ModItems.HARDEND_RESIN))
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT),
                AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT)
            )
            .save(provider, AnvilCraftPigsPlus.of("comparator").toString());
    }
}
