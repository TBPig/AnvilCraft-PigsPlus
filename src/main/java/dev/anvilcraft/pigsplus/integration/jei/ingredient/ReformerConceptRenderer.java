package dev.anvilcraft.pigsplus.integration.jei.ingredient;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

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
    @SuppressWarnings("removal") // JEI 19.50 still requires implementing this deprecated-for-removal overload.
    public List<Component> getTooltip(ReformerConcept ingredient, TooltipFlag tooltipFlag) {
        return this.getTooltip(ingredient, Item.TooltipContext.EMPTY, null, tooltipFlag);
    }

    @Override
    public List<Component> getTooltip(
        ReformerConcept ingredient,
        Item.TooltipContext tooltipContext,
        @Nullable Player player,
        TooltipFlag tooltipFlag
    ) {
        return List.of(Component.translatable(ingredient.translationKey()));
    }
}
