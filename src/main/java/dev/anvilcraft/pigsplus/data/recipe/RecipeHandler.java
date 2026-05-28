package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModDataComponentPredicates;
import dev.dubhe.anvilcraft.item.property.predicate.ItemSavedEntityPredicate;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.BulgingRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemCompressRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemInjectRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.TimeWarpRecipe;
import dev.dubhe.anvilcraft.recipe.mineral.MineralFountainRecipe;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class RecipeHandler {
    public static void init(RegistrumRecipeProvider provider) {
        VanillaRecipesLoader.init(provider);
        SuperHeatingRecipeLoader.init(provider);
        MultipleToOneSmithingRecipeLoader.init(provider);

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
                    .hasComponents(new DataComponentMatchers(
                        DataComponentExactPredicate.builder().build(),
                        Map.of(
                            ModDataComponentPredicates.SAVED_ENTITY.get(),
                            ItemSavedEntityPredicate.of(EntityType.CHICKEN)
                        )
                    ))
                    .build()
            )
            .result(AddonBlocks.AUTO_CHICKEN)
            .save(provider);

        TimeWarpRecipe.builder()
            .requires(Items.ENDER_PEARL, 6)
            .requires(Items.END_STONE, 18)
            .result(AddonItems.ENDER_COMPONENT)
            .save(provider);

        TimeWarpRecipe.builder()
            .requires(AddonItems.ECHO_GEODE)
            .result(AddonBlocks.BUDDING_ECHO_SHARD)
            .save(provider);

        BulgingRecipe.builder()
            .cauldron(Blocks.WATER_CAULDRON)
            .consume(250)
            .requires(AddonItems.ENDER_SEED)
            .result(Items.ENDER_PEARL)
            .save(provider, AnvilCraftPigsPlus.of("bulging/water_to_ender_pearl"));

        BulgingRecipe.builder()
            .cauldron(ModBlocks.EXP_FLUID_CAULDRON.get())
            .consume(250)
            .requires(AddonItems.ENDER_SEED)
            .result(Items.ENDER_PEARL, 3)
            .save(provider, AnvilCraftPigsPlus.of("bulging/exp_fluid_to_ender_pearl"));
    }
}
