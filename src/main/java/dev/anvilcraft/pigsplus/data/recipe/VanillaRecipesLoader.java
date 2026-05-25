package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

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
    }
}
