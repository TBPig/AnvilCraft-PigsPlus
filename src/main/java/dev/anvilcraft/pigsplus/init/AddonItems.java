package dev.anvilcraft.pigsplus.init;


import com.tterrag.registrate.util.entry.ItemEntry;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

public class AddonItems {
    static {
        REGISTRATE.defaultCreativeTab(AddonItemGroups.ADDON_ITEMS.getKey());
    }

    public static final ItemEntry<Item> KARAKURI_COMPONENT = REGISTRATE
        .item("karakuri_component", Item::new)
        .recipe((ctx, provider) -> {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get())
                .requires(Items.REDSTONE_BLOCK)
                .requires(ModItems.BRASS_INGOT)
                .requires(ModItems.BRASS_INGOT)
                .requires(ModItems.BRASS_INGOT)
                .unlockedBy(AnvilCraftDatagen.hasItem(Items.REDSTONE_BLOCK), AnvilCraftDatagen.has(Items.REDSTONE_BLOCK))
                .unlockedBy(AnvilCraftDatagen.hasItem(ModItems.BRASS_INGOT), AnvilCraftDatagen.has(ModItems.BRASS_INGOT))
                .save(provider);
        }).register();

    public static void register() {
    }
}