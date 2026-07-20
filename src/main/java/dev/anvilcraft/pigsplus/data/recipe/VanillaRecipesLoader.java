package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

public class VanillaRecipesLoader {
    public static void init(RegistrumRecipeProvider provider) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_BOARD, 16)
            .pattern("W")
            .pattern("R")
            .define('W', AddonItems.KARAKURI_COMPONENT)
            .define('R', ModItems.HARDEND_RESIN)
            .unlockedBy("hasitem", AnvilCraftDatagen.has(AddonItems.KARAKURI_COMPONENT))
            .save(provider, AnvilCraftPigsPlus.of("circuit_board"));
    }
}
