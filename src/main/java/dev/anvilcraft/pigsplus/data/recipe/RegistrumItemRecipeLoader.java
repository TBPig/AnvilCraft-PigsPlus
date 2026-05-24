package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class RegistrumItemRecipeLoader {
    public static <T extends Item> void karakuriComponent(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapelessRecipeBuilder.shapeless(lookup, RecipeCategory.MISC, ctx.get())
            .requires(Items.REDSTONE_BLOCK)
            .requires(ModItemTags.BRASS_INGOTS)
            .requires(ModItemTags.BRASS_INGOTS)
            .requires(ModItemTags.BRASS_INGOTS)
            .unlockedBy(AnvilCraftDatagen.hasItem(ModItemTags.BRASS_INGOTS), AnvilCraftDatagen.has(lookup, ModItemTags.BRASS_INGOTS))
            .save(provider);
    }

    public static <T extends Item> void chaoticRawOre(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapelessRecipeBuilder.shapeless(lookup, RecipeCategory.MISC, ctx.get(), 9)
            .requires(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK),
                AnvilCraftDatagen.has(lookup, AddonBlocks.CHAOTIC_RAW_ORE_BLOCK)
            )
            .save(provider);
    }

    public static <T extends Item> void portableWirelessCharger(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapedRecipeBuilder.shaped(lookup, RecipeCategory.MISC, ctx.get())
            .pattern("SAS")
            .pattern("SBS")
            .pattern("SCS")
            .define('S', Items.COPPER_INGOT)
            .define('A', ModBlocks.POWER_CONVERTER_BIG)
            .define('B', AddonItems.KARAKURI_COMPONENT)
            .define('C', ModBlocks.CHARGER)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT), AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT))
            .save(provider);
    }

    public static <T extends Item> void mengerSpongeStaff(DataGenContext<Item, T> ctx, RegistrumRecipeProvider provider) {
        HolderGetter<Item> lookup = provider.getItems();
        ShapelessRecipeBuilder.shapeless(lookup, RecipeCategory.MISC, ctx.get())
            .requires(ModItems.ANVIL_HAMMER)
            .requires(ModBlocks.MENGER_SPONGE)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT), AnvilCraftDatagen.has(lookup, AddonItems.KARAKURI_COMPONENT))
            .save(provider);
    }
}
