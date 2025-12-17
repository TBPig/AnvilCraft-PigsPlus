package dev.anvilcraft.pigsplus.integration.jei.recipe;

import com.google.common.collect.ImmutableList;
import dev.anvilcraft.lib.recipe.component.ChanceItemStack;
import dev.anvilcraft.lib.recipe.component.ItemIngredientPredicate;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.integration.jei.recipe.EndPortalConversionRecipe;
import net.minecraft.world.item.Item;

import java.util.List;

import static dev.anvilcraft.pigsplus.util.EnderComponentConversionUtil.ConversionChance;

public class BetterEndPortalConversionRecipe extends EndPortalConversionRecipe {
    // TODO:之后自己写个物品落入传送门时的逻辑
    public final List<ItemIngredientPredicate> ingredients;
    public final List<ChanceItemStack> results;

    public BetterEndPortalConversionRecipe(Item itemInput) {
        super(ModBlocks.ROYAL_ANVIL.get(), 0.5f); // 随便填的
        this.ingredients = ImmutableList.of(ItemIngredientPredicate.Builder.item().of(itemInput).build());
        ImmutableList.Builder<ChanceItemStack> builder = ImmutableList.builder();
        builder.add(ChanceItemStack.of(AddonItems.ENDER_COMPONENT.asStack(), ConversionChance));

        builder.add(ChanceItemStack.of(ModItems.LEVITATION_POWDER.asStack(), 1.0f - ConversionChance));

        this.results = builder.build();

    }

    public static ImmutableList<EndPortalConversionRecipe> getAllRecipes() {
        ImmutableList.Builder<EndPortalConversionRecipe> builder = ImmutableList.builder();
        builder.add(new BetterEndPortalConversionRecipe(AddonItems.KARAKURI_COMPONENT.asItem()));
        return builder.build();
    }
}
