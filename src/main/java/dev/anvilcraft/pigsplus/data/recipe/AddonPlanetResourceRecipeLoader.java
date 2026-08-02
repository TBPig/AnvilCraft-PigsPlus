package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * 附属模组的星球资源配方数据生成器，写法与本体 PlanetResourceRecipeLoader 保持一致。
 */
public class AddonPlanetResourceRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        saveRecipe(provider, "void_wasteland", new PlanetResourceRecipe(
            PlanetResourceRecipe.Category.WASTELAND,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(new PlanetResourceRecipe.WastelandData(
                List.of(
                    new PlanetResourceRecipe.WeightedEntry("anvilcraft:reinforced_concrete_gray", 60),
                    new PlanetResourceRecipe.WeightedEntry("anvilcraft:circuit_board", 30),
                    new PlanetResourceRecipe.WeightedEntry("anvilcraft:processor", 5),
                    new PlanetResourceRecipe.WeightedEntry("anvilcraft:void_matter", 3),
                    new PlanetResourceRecipe.WeightedEntry("anvilcraft:negative_matter_nugget", 2)
                ),
                35,
                10
            ))
        ));
    }

    private static void saveRecipe(RecipeOutput output, String name, PlanetResourceRecipe recipe) {
        ResourceLocation id = AnvilCraftPigsPlus.of("planet_resource/" + name);
        Advancement.Builder advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR);
        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/")));
    }
}
