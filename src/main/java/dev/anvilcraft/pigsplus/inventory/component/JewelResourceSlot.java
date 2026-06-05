package dev.anvilcraft.pigsplus.inventory.component;

import dev.dubhe.anvilcraft.init.recipe.ModRecipeTypes;
import dev.dubhe.anvilcraft.recipe.sync.RecipesRecord;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class JewelResourceSlot extends ResourceHandlerSlot {
    Level level;

    public JewelResourceSlot(
        ResourceHandler<ItemResource> handler,
        IndexModifier<ItemResource> slotModifier,
        int handlerSlot,
        int xPosition,
        int yPosition,
        Level level
    ) {
        super(handler, slotModifier, handlerSlot, xPosition, yPosition);
        this.level = level;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return RecipesRecord.getRecipes(this.level).byType(ModRecipeTypes.JEWEL_CRAFTING.get())
            .stream()
            .anyMatch(holder -> holder.value().source().test(stack));
    }
}
