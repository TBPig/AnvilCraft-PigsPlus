package dev.anvilcraft.pigsplus.integration.jei.ingredient;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ReformerConceptRenderer implements IIngredientRenderer<ReformerConcept> {
    @Override
    public void render(GuiGraphics guiGraphics, ReformerConcept ingredient) {
        guiGraphics.blit(
            ingredient.icon(),
            0,
            0,
            0,
            0,
            16,
            16,
            16,
            16
        );
    }

    @Override
    public List<Component> getTooltip(ReformerConcept ingredient, TooltipFlag tooltipFlag) {
        return List.of(Component.translatable(ingredient.translationKey()));
    }
}
