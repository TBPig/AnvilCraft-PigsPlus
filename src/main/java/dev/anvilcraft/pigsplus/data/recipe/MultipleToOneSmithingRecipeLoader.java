package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.recipe.multiple.EightToOneSmithingRecipe;
import net.minecraft.world.item.Items;

public class MultipleToOneSmithingRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        EightToOneSmithingRecipe.builder()
            .material(ModItems.EARTH_CORE_SHARD)
            .input(Items.RAW_COPPER)
            .input(Items.RAW_GOLD)
            .input(ModItems.RAW_ZINC)
            .input(ModItems.RAW_TIN)
            .input(ModItems.RAW_TITANIUM)
            .input(ModItems.RAW_TUNGSTEN)
            .input(ModItems.RAW_LEAD)
            .input(ModItems.RAW_SILVER)
            .resultMerge(AddonItems.CHAOTIC_RAW_ORE)
            .save(provider);
    }
}
