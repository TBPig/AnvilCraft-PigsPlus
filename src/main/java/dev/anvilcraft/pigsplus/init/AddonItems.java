package dev.anvilcraft.pigsplus.init;


import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import dev.anvilcraft.pigsplus.item.KarakuriComponentItem;
import dev.anvilcraft.pigsplus.item.MengerSpongeHolyStaffItem;
import dev.anvilcraft.pigsplus.item.MengerSpongeStaffItem;
import dev.anvilcraft.pigsplus.item.PortableWirelessChargerItem;
import dev.anvilcraft.pigsplus.item.GridAdapterItem;
import dev.dubhe.anvilcraft.data.AnvilCraftDatagen;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.item.property.component.Eternal;
import dev.dubhe.anvilcraft.util.DataGenUtil;
import dev.dubhe.anvilcraft.util.registrater.ModelProviderUtil;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

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

    public static final ItemEntry<Item> CELESTIAL_REFORMER_COMPONENT = REGISTRATE
        .item("celestial_reformer_component", Item::new)
        .properties(properties -> properties
            .fireResistant()
            .rarity(Rarity.EPIC)
            .component(ModComponents.ETERNAL, Eternal.INSTANCE)
        )
        .tag(ModItemTags.EXPLOSION_PROOF)
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), 4)
            .pattern("TKT")
            .pattern("K K")
            .pattern("TKT")
            .define('K', AddonItems.KARAKURI_COMPONENT)
            .define('T', ModItems.TRANSCENDIUM_INGOT)
            .unlockedBy(AnvilCraftDatagen.hasItem(ModItems.TRANSCENDIUM_INGOT), AnvilCraftDatagen.has(ModItems.TRANSCENDIUM_INGOT))
            .save(provider))
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

    public static final ItemEntry<GridAdapterItem> GRID_ADAPTER = REGISTRATE
        .item("grid_adapter", GridAdapterItem::new)
        .properties(properties -> properties.stacksTo(1))
        .model((ctx, prov) -> {
            ItemModelBuilder outputModel = prov.getBuilder("grid_adapter_output")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/grid_adapter_out");
            ItemModelBuilder inputModel = prov.getBuilder("grid_adapter_input")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/grid_adapter");
            prov.generated(ctx.lazy())
                .override()
                .predicate(
                    ResourceLocation.withDefaultNamespace("custom_model_data"),
                    GridAdapterItem.INPUT_MODE
                )
                .model(new ModelFile.UncheckedModelFile(inputModel.getUncheckedLocation()))
                .end()
                .override()
                .predicate(
                    ResourceLocation.withDefaultNamespace("custom_model_data"),
                    GridAdapterItem.OUTPUT_MODE
                )
                .model(new ModelFile.UncheckedModelFile(outputModel.getUncheckedLocation()))
                .end();
        })
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
            .pattern(" D ")
            .pattern("ABA")
            .pattern(" C ")
            .define('A', AddonItems.KARAKURI_COMPONENT)
            .define('B', ModItems.MULTIPHASE_MATTER)
            .define('C', AddonBlocks.ADJUSTABLE_POWER_CONVERTER)
            .define('D', Items.LIGHTNING_ROD)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.KARAKURI_COMPONENT),
                AnvilCraftDatagen.has(AddonItems.KARAKURI_COMPONENT)
            )
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

    public static final ItemEntry<MengerSpongeHolyStaffItem> MENGER_SPONGE_HOLY_STAFF = REGISTRATE
        .item("menger_sponge_holy_staff", MengerSpongeHolyStaffItem::new)
        .properties(properties -> properties
            .stacksTo(1)
            .fireResistant()
            .rarity(Rarity.EPIC)
            .component(ModComponents.ETERNAL, Eternal.INSTANCE)
        )
        .tag(ModItemTags.EXPLOSION_PROOF)
        .model(DataGenUtil::noExtraModelOrState)
        .recipe((ctx, provider) -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())
            .pattern("A")
            .pattern("B")
            .define('A', AddonItems.MENGER_SPONGE_STAFF)
            .define('B', ModItems.TRANSCENDIUM_INGOT)
            .unlockedBy(
                AnvilCraftDatagen.hasItem(AddonItems.MENGER_SPONGE_STAFF),
                AnvilCraftDatagen.has(AddonItems.MENGER_SPONGE_STAFF)
            )
            .unlockedBy(AnvilCraftDatagen.hasItem(ModItems.TRANSCENDIUM_INGOT), AnvilCraftDatagen.has(ModItems.TRANSCENDIUM_INGOT))
            .save(provider))
        .register();

    public static final ItemEntry<BucketItem> VOID_ACID_BUCKET = REGISTRATE
        .item("void_acid_bucket", props -> new BucketItem(AddonFluids.VOID_ACID.get(), props))
        .initialProperties(() -> new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET))
        .model(ModelProviderUtil::bucket)
        .register();

    public static final ItemEntry<Item> UNIVERSAL_REDSTONE_COMPONENT = REGISTRATE
        .item("universal_redstone_component", Item::new)
        .model(DataGenUtil::noExtraModelOrState)
        .register();

    public static void register() {
    }
}
