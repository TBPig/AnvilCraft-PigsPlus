package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import dev.anvilcraft.lib.v2.util.predicate.ChanceBlockState;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemSubPredicates;
import dev.dubhe.anvilcraft.item.property.predicate.ItemSavedEntityPredicate;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemCompressRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemInjectRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.SolidLiquidRecipe;
import dev.dubhe.anvilcraft.recipe.mineral.AroundRequirement;
import dev.dubhe.anvilcraft.recipe.mineral.MineralFountainActiveRecipe;
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
        SoulVineGrowthRecipeLoader.init(provider);
        BeamCastingRecipeLoader.init(provider);
        ProceduralProcessRecipeLoader.init(provider);
        CelestialReformerRecipeLoader.init(provider);
        AddonSpecialCelestialBodyRecipeLoader.init(provider);

        MineralFountainActiveRecipe.builder()
            .fromBlock(BlockStatePredicate.builder().of(Blocks.DEEPSLATE).build())
            .require(new AroundRequirement(BlockStatePredicate.builder().of(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK.get()).build(), 4))
            .toBlock(new ChanceBlockState(AddonBlocks.DEEPSLATE_CHAOTIC_ORE.get().defaultBlockState(), 1.0F))
            .save(provider, "deepslate_chaotic_ore");

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

        SolidLiquidRecipe.builder()
            .cauldron(Blocks.WATER_CAULDRON)
            .consume(250)
            .requires(AddonItems.ENDER_SEED)
            .result(Items.ENDER_PEARL)
            .save(provider, AnvilCraftPigsPlus.of("bulging/water_to_ender_pearl"));

        SolidLiquidRecipe.builder()
            .cauldron(ModBlocks.EXP_FLUID_CAULDRON.get())
            .consume(250)
            .requires(AddonItems.ENDER_SEED)
            .result(Items.ENDER_PEARL, 3)
            .save(provider, AnvilCraftPigsPlus.of("bulging/exp_fluid_to_ender_pearl"));
    }
}
