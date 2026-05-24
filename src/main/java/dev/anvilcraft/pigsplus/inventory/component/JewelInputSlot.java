package dev.anvilcraft.pigsplus.inventory.component;

import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.dubhe.anvilcraft.recipe.JewelCraftingRecipe;
import dev.dubhe.anvilcraft.util.RecipeUtil;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class JewelInputSlot extends ResourceHandlerSlot {
    @Getter
    @Nullable
    private ItemIngredientPredicate ingredient;
    @Getter
    @Nullable
    private List<ItemStack> ingredientItems;

    public JewelInputSlot(
        ResourceHandler<ItemResource> handler,
        IndexModifier<ItemResource> slotModifier,
        int handlerSlot,
        int xPosition,
        int yPosition
    ) {
        super(handler, slotModifier, handlerSlot, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (this.ingredient == null) {
            return false;
        }
        if (!this.ingredient.test(stack)) {
            return false;
        }
        return super.mayPlace(stack);
    }

    public void updateIngredient(JewelCraftingRecipe recipe) {
        var ingredients = recipe.ingredients();
        if (this.getSlotIndex() > ingredients.size() - 1) {
            this.ingredient = null;
            this.ingredientItems = null;
        } else {
            var entry = ingredients.get(this.getSlotIndex());
            this.ingredient = entry;
            this.ingredientItems = RecipeUtil.getItems(entry, BuiltInRegistries.ITEM);
        }
    }
}
