package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

public class VanillaRecipesLoader {
    public static void init(RegistrumRecipeProvider provider) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CIRCUIT_BOARD, 16)
            .requires(ModItems.HARDEND_RESIN)
            .requires(ModItems.HARDEND_RESIN)
            .requires(AddonItems.KARAKURI_COMPONENT)
            .unlockedBy("hasitem", AnvilCraftDatagen.has(AddonItems.KARAKURI_COMPONENT))
            .save(provider, AnvilCraftPigsPlus.of("circuit_board"));
    }
}
