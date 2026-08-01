package dev.anvilcraft.pigsplus.integration.jei.util;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

public final class AddonJeiUtil {
    private AddonJeiUtil() {
    }

    /**
     * 添加带数量的物品输入格
     */
    public static IRecipeSlotBuilder addItemInputSlot(
        IRecipeLayoutBuilder builder,
        int x,
        int y,
        ItemStack stack
    ) {
        return builder.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStack(stack);
    }

    /**
     * 添加带数量的物品输入格
     */
    public static IRecipeSlotBuilder addItemInputSlot(
        IRecipeLayoutBuilder builder,
        int x,
        int y,
        ItemLike item,
        int count
    ) {
        return addItemInputSlot(builder, x, y, new ItemStack(item, count));
    }

    /**
     * 添加带数量的流体输入格
     */
    public static IRecipeSlotBuilder addFluidInputSlot(
        IRecipeLayoutBuilder builder,
        int x,
        int y,
        Fluid fluid,
        long amount
    ) {
        return builder.addSlot(RecipeIngredientRole.INPUT, x, y)
            .setFluidRenderer(amount, false, 16, 16)
            .addFluidStack(fluid, amount);
    }
}
