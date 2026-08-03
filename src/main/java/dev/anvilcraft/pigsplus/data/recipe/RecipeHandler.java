package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemSubPredicates;
import dev.dubhe.anvilcraft.item.property.predicate.ItemSavedEntityPredicate;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemCompressRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemInjectRecipe;
import dev.dubhe.anvilcraft.recipe.mineral.MineralFountainRecipe;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class RecipeHandler {
    public static void init(RegistrumRecipeProvider provider) {
        VanillaRecipesLoader.init(provider);
        SuperHeatingRecipeLoader.init(provider);
        TimeWarpRecipeLoader.init(provider);
        MultipleToOneSmithingRecipeLoader.init(provider);
        PrecisionElectromagneticProcessingLoader.init(provider);
        ProceduralProcessRecipeLoader.init(provider);
        CelestialReformerRecipeLoader.init(provider);
        AddonSpecialCelestialBodyRecipeLoader.init(provider);
        AddonPlanetResourceRecipeLoader.init(provider);


        MineralFountainRecipe.builder()
            .needBlock(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK.get())
            .fromBlock(Blocks.DEEPSLATE)
            .toBlock(AddonBlocks.DEEPSLATE_CHAOTIC_ORE.get())
            .save(provider);

        ItemInjectRecipe.builder()
            .requires(Items.ECHO_SHARD)
            .inputBlock(Blocks.BUDDING_AMETHYST)
            .resultBlock(AddonBlocks.BUDDING_ECHO_SHARD)
            .save(provider);

        ItemCompressRecipe.builder()
            .requires(AddonItems.KARAKURI_COMPONENT, 2)
            .requires(
                ItemIngredientPredicate
                    .of(ModBlocks.RESIN_BLOCK.asItem())
                    .withSubPredicate(
                        ModItemSubPredicates.SAVED_ENTITY.get(),
                        ItemSavedEntityPredicate.of(EntityType.CHICKEN)
                    )
                    .build()
            )
            .result(AddonBlocks.AUTO_CHICKEN)
            .save(provider);
    }
}
