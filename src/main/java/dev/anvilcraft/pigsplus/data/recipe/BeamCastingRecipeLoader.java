package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.recipe.BeamCastingRecipe;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;

public class BeamCastingRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        BeamCastingRecipe.builder()
            .requires(ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK)
            .requires(ModItemTags.GEMS_SAPPHIRE, 2)
            .requires(ModItems.ROYAL_STEEL_INGOT)
            .requires(AddonItems.KARAKURI_COMPONENT)
            .result(ModBlocks.HEAT_COLLECTOR)
            .save(provider);
    }
}
