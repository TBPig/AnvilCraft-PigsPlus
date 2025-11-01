package dev.anvilcraft.pigsplus.data.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;

public class RecipeHandler {
    public static void init(RegistrateRecipeProvider provider) {
        VanillaRecipesLoader.init(provider);
    }
}
