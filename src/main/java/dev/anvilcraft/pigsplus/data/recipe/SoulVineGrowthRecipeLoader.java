package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.recipe.SoulVineGrowthRecipe;
import net.minecraft.world.level.block.Blocks;

public class SoulVineGrowthRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        SoulVineGrowthRecipe.builder()
            .requires(Blocks.TWISTING_VINES)
            .result(Blocks.WEEPING_VINES)
            .save(provider, AnvilCraftPigsPlus.of("soul_vine_growth/weeping_vines"));
        SoulVineGrowthRecipe.builder()
            .fluid(Blocks.WATER_CAULDRON)
            .consume(250)
            .requires(Blocks.TWISTING_VINES)
            .result(Blocks.WEEPING_VINES, 3)
            .save(provider, AnvilCraftPigsPlus.of("soul_vine_growth/weeping_vines_cauldron"));
    }
}
