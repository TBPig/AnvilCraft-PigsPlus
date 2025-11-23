package dev.anvilcraft.pigsplus.init;


import com.tterrag.registrate.util.entry.ItemEntry;
import dev.anvilcraft.pigsplus.item.KarakuriComponentItem;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

public class AddonItems {
    static {
        REGISTRATE.defaultCreativeTab(AddonItemGroups.ADDON_ITEMS.getKey());
    }

    public static final ItemEntry<KarakuriComponentItem> KARAKURI_COMPONENT = REGISTRATE
        .item("karakuri_component", KarakuriComponentItem::new)
        .recipe((ctx, provider) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get())
            .requires(Items.REDSTONE_BLOCK)
            .requires(ModItemTags.BRASS_INGOTS)
            .requires(ModItemTags.BRASS_INGOTS)
            .requires(ModItemTags.BRASS_INGOTS)
            .unlockedBy(AnvilCraftDatagen.hasItem(ModItemTags.BRASS_INGOTS), AnvilCraftDatagen.has(ModItemTags.BRASS_INGOTS))
            .save(provider))
        .register();

    public static final ItemEntry<Item> SPIRITUAL_COMPONENT = REGISTRATE
        .item("spiritual_component", Item::new)
        .register();

    public static final ItemEntry<Item> CHAOTIC_RAW_ORE = REGISTRATE
        .item("chaotic_raw_ore", Item::new)
        .recipe((ctx, provider) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get(), 9)
            .requires(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK),
                AnvilCraftDatagen.has(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK)
            )
            .save(provider))
        .register();

    public static void register() {
    }
}