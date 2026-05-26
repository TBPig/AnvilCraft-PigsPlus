package dev.anvilcraft.pigsplus.init;


import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import dev.anvilcraft.pigsplus.item.KarakuriComponentItem;
import dev.anvilcraft.pigsplus.item.MengerSpongeStaffItem;
import dev.anvilcraft.pigsplus.item.PortableWirelessChargerItem;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.util.DataGenUtil;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

@SuppressWarnings("unused")
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

    public static final ItemEntry<Item> ENDER_COMPONENT = REGISTRATE
        .item("ender_component", Item::new)
        .register();

    public static final ItemEntry<Item> ECHO_GEODE = REGISTRATE
        .item("echo_geode", Item::new)
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

    public static final ItemEntry<PortableWirelessChargerItem> PORTABLE_WIRELESS_CHARGER = REGISTRATE
        .item("portable_wireless_charger", PortableWirelessChargerItem::new)
        .properties((properties) -> properties.stacksTo(1))
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
            .pattern("SAS")
            .pattern("SBS")
            .pattern("SCS")
            .define('S', Items.COPPER_INGOT)
            .define('A', ModBlocks.POWER_CONVERTER_BIG)
            .define('B', AddonItems.KARAKURI_COMPONENT)
            .define('C', ModBlocks.CHARGER)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT), AnvilCraftDatagen.has(AddonItems.KARAKURI_COMPONENT))
            .save(provider))
        .register();

    public static final ItemEntry<MengerSpongeStaffItem> MENGER_SPONGE_STAFF = REGISTRATE
        .item("menger_sponge_staff", MengerSpongeStaffItem::new)
        .properties((properties) -> properties.stacksTo(1))
        .model(DataGenUtil::noExtraModelOrState)
        .recipe((ctx, provider) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ctx.get())
            .requires(ModItems.ANVIL_HAMMER)
            .requires(ModBlocks.MENGER_SPONGE)
            .unlockedBy(AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT), AnvilCraftDatagen.has(AddonItems.KARAKURI_COMPONENT))
            .save(provider))
        .register();

    public static void register() {
    }
}