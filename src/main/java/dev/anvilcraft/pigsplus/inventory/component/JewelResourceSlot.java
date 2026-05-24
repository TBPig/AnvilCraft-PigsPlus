package dev.anvilcraft.pigsplus.inventory.component;

import dev.dubhe.anvilcraft.init.recipe.ModRecipeTypes;
import dev.dubhe.anvilcraft.recipe.sync.RecipesRecord;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class JewelResourceSlot extends ResourceHandlerSlot {

    public JewelResourceSlot(
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
        return RecipesRecord.RECIPES.byType(ModRecipeTypes.JEWEL_CRAFTING.get())
            .stream()
            .anyMatch(holder -> holder.value().source().test(stack));
    }
}
