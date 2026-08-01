package dev.anvilcraft.pigsplus.integration.jei.ingredient;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ReformerConceptHelper implements IIngredientHelper<ReformerConcept> {
    @Override
    public IIngredientType<ReformerConcept> getIngredientType() {
        return ReformerConcept.TYPE;
    }

    @Override
    public String getDisplayName(ReformerConcept ingredient) {
        return Component.translatable(ingredient.translationKey()).getString();
    }

    @Override
    @SuppressWarnings("removal")
    public String getUniqueId(ReformerConcept ingredient, UidContext context) {
        return ingredient.icon().toString();
    }

    @Override
    public ResourceLocation getResourceLocation(ReformerConcept ingredient) {
        return ingredient.icon();
    }

    @Override
    public ReformerConcept copyIngredient(ReformerConcept ingredient) {
        return ingredient;
    }

    @Override
    public String getErrorInfo(@Nullable ReformerConcept ingredient) {
        return ingredient == null ? "null" : ingredient.icon().toString();
    }
}
