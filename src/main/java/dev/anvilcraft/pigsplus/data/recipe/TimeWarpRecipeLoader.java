package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.TimeWarpRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import static dev.dubhe.anvilcraft.data.recipe.util.RecipeLoaderUtil.getName;

public class TimeWarpRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        TimeWarpRecipe.builder()
            .requires(Items.ENDER_PEARL, 6)
            .requires(Items.END_STONE, 18)
            .result(AddonItems.ENDER_COMPONENT)
            .save(provider);

        TimeWarpRecipe.builder()
            .requires(AddonItems.ECHO_GEODE)
            .result(AddonBlocks.BUDDING_ECHO_SHARD)
            .save(provider);

        // ingotFromAcid
        ingotFromAcid(provider, Tags.Items.RAW_MATERIALS_COPPER, Items.COPPER_INGOT);
        ingotFromAcid(provider, Tags.Items.RAW_MATERIALS_IRON, Items.IRON_INGOT);
        ingotFromAcid(provider, Tags.Items.RAW_MATERIALS_GOLD, Items.GOLD_INGOT);
        ingotFromAcid(provider, ModItemTags.RAW_ZINC, ModItems.ZINC_INGOT);
        ingotFromAcid(provider, ModItemTags.RAW_TIN, ModItems.TIN_INGOT);
        ingotFromAcid(provider, ModItemTags.RAW_TITANIUM, ModItems.TITANIUM_INGOT);
        ingotFromAcid(provider, ModItemTags.RAW_TUNGSTEN, ModItems.TUNGSTEN_INGOT);
        ingotFromAcid(provider, ModItemTags.RAW_LEAD, ModItems.LEAD_INGOT);
        ingotFromAcid(provider, ModItemTags.RAW_SILVER, ModItems.SILVER_INGOT);
        ingotFromAcid(provider, ModItemTags.RAW_URANIUM, ModItems.URANIUM_INGOT);
    }

    private static void ingotFromAcid(RegistrumRecipeProvider provider, TagKey<Item> raw, ItemLike result) {
        TimeWarpRecipe.builder()
            .requires(raw, 8)
            .fluid(AddonBlocks.VOID_ACID_CAULDRON.get())
            .consume(250)
            .result(result, 32)
            .save(provider, AnvilCraftPigsPlus.of("time_warp/raw_with_acid/%s".formatted(getName(result))));
    }
}
