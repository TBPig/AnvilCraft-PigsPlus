package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class AddonSpecialCelestialBodyRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        saveRecipe(provider, "fulgora", new SpecialCelestialBodyRecipe(
            "fulgora",
            "planet_wet",
            false,
            45,
            13,
            18,
            20,
            true,
            Optional.of(LiquidCoverage.HIGH),
            4,
            4,
            12.5f,
            List.of(
                ResourceLocation.withDefaultNamespace("lightning_rod"),
                ResourceLocation.parse("anvilcraft_pigsplus:karakuri_component"),
                ResourceLocation.parse("anvilcraft_pigsplus:spiritual_component"),
                ResourceLocation.parse("anvilcraft_pigsplus:ender_component"),
                ResourceLocation.parse("anvilcraft:circuit_board"),
                ResourceLocation.parse("anvilcraft:processor"),
                ResourceLocation.parse("anvilcraft:magnetoelectric_core")
            ),
            List.of(
                entry("anvilcraft:circuit_board", 35),
                entry("anvilcraft:processor", 35),
                entry("anvilcraft_pigsplus:karakuri_component", 20),
                entry("anvilcraft_pigsplus:spiritual_component", 5),
                entry("anvilcraft_pigsplus:ender_component", 5),
                entry("anvilcraft:magnetoelectric_core", 5)
            ),
            List.of(
                entry("anvilcraft:oil", 100)
            ),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of()
        ));
    }

    private static void saveRecipe(RecipeOutput output, String name, SpecialCelestialBodyRecipe recipe) {
        ResourceLocation id = AnvilCraftPigsPlus.of("special_celestial_body/" + name);
        Advancement.Builder advancement = output.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR);
        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/")));
    }

    private static SpecialCelestialBodyRecipe.WeightedEntry entry(String id, int weight) {
        return new SpecialCelestialBodyRecipe.WeightedEntry(id, weight);
    }
}
