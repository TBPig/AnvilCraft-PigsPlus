package dev.anvilcraft.pigsplus.integration.jei;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.modification.ReformerModifications;
import dev.anvilcraft.pigsplus.api.requirement.CelestialReformerRequirements;
import dev.anvilcraft.pigsplus.api.requirement.ReformerRequirement;
import dev.anvilcraft.pigsplus.integration.jei.category.PrecisionElectromagneticProcessingCategory;
import dev.anvilcraft.pigsplus.integration.jei.category.CelestialReformerCategory;
import dev.anvilcraft.pigsplus.integration.jei.ingredient.ReformerConcept;
import dev.anvilcraft.pigsplus.integration.jei.ingredient.ReformerConceptHelper;
import dev.anvilcraft.pigsplus.integration.jei.ingredient.ReformerConceptRenderer;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.anvilcraft.pigsplus.recipe.PrecisionElectromagneticProcessingRecipe;
import dev.anvilcraft.pigsplus.util.ReformerIcons;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static dev.anvilcraft.pigsplus.item.KarakuriComponentItem.PROBABILITY;
import static dev.anvilcraft.pigsplus.util.EnderComponentConversionUtil.ConversionChance;

@JeiPlugin
public class AddonJeiPlugin implements IModPlugin {

    public static final RecipeType<RecipeHolder<PrecisionElectromagneticProcessingRecipe>> PRECISION_ELECTROMAGNETIC_PROCESSING =
        createRecipeHolderType("precision_electromagnetic_processing");
    public static final RecipeType<RecipeHolder<CelestialReformerRecipe>> CELESTIAL_REFORMER =
        createRecipeHolderType("celestial_reformer");

    @Override
    public ResourceLocation getPluginUid() {
        return AnvilCraftPigsPlus.of("jei_plugin");
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(
            ReformerConcept.TYPE,
            collectConcepts(),
            new ReformerConceptHelper(),
            new ReformerConceptRenderer(),
            ReformerConcept.CODEC
        );
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new PrecisionElectromagneticProcessingCategory(guiHelper));
        registration.addRecipeCategories(new CelestialReformerCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addItemStackInfo(
            AddonItems.SPIRITUAL_COMPONENT.asStack(),
            Component.translatable("jei.anvilcraft.pigsplus.info.spiritual_component", PROBABILITY*100)
        );

        registration.addItemStackInfo(
            AddonItems.ENDER_COMPONENT.asStack(),
            Component.translatable("jei.anvilcraft.pigsplus.info.ender_component", ConversionChance * 100)
        );

        PrecisionElectromagneticProcessingCategory.registerRecipes(registration);
        CelestialReformerCategory.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        PrecisionElectromagneticProcessingCategory.registerRecipeCatalysts(registration);
        CelestialReformerCategory.registerRecipeCatalysts(registration);
    }

    private static <R extends net.minecraft.world.item.crafting.Recipe<?>> RecipeType<RecipeHolder<R>> createRecipeHolderType(String name) {
        return RecipeType.createRecipeHolderType(AnvilCraftPigsPlus.of(name));
    }

    private static Collection<ReformerConcept> collectConcepts() {
        Set<ResourceLocation> icons = new LinkedHashSet<>();
        for (ReformerModification modification : ReformerModifications.REGISTRY) {
            icons.add(modification.getIcon());
        }
        for (Map.Entry<ResourceKey<ReformerRequirement>, ReformerRequirement> entry
            : CelestialReformerRequirements.REGISTRY.entrySet()) {
            icons.add(entry.getValue().getIcon());
        }
        icons.add(ReformerIcons.laserIcon());
        return icons.stream().map(ReformerConcept::new).toList();
    }
}
