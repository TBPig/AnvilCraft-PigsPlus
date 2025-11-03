package dev.anvilcraft.pigsplus.data.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.recipe.mineral.MineralFountainRecipe;
import net.minecraft.world.level.block.Blocks;

public class RecipeHandler {
    public static void init(RegistrateRecipeProvider provider) {
        VanillaRecipesLoader.init(provider);
        SuperHeatingRecipeLoader.init(provider);
        MultipleToOneSmithingRecipeLoader.init(provider);


        MineralFountainRecipe.builder()
            .needBlock(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK.get())
            .fromBlock(Blocks.DEEPSLATE)
            .toBlock(AddonBlocks.DEEPSLATE_CHAOTIC_ORE.get())
            .save(provider);
    }
}
