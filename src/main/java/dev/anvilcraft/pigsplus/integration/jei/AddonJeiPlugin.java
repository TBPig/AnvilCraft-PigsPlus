package dev.anvilcraft.pigsplus.integration.jei;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.integration.jei.category.PrecisionElectromagneticProcessingCategory;
import dev.anvilcraft.pigsplus.integration.jei.category.CelestialReformerCategory;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.anvilcraft.pigsplus.recipe.PrecisionElectromagneticProcessingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

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
}
