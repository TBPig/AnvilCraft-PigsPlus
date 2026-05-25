package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.SuperHeatingRecipe;
import net.minecraft.world.item.Items;

public class SuperHeatingRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        SuperHeatingRecipe.builder()
            .requires(AddonItems.CHAOTIC_RAW_ORE)
            .result(Items.COPPER_INGOT, 0.3f)
            .result(Items.GOLD_INGOT, 0.3f)
            .result(ModItems.ZINC_INGOT, 0.3f)
            .result(ModItems.TIN_INGOT, 0.3f)
            .result(ModItems.TITANIUM_INGOT, 0.3f)
            .result(ModItems.TUNGSTEN_INGOT, 0.3f)
            .result(ModItems.LEAD_INGOT, 0.3f)
            .result(ModItems.SILVER_INGOT, 0.3f)
            .save(provider, AnvilCraftPigsPlus.of("super_heating/chaotic_raw_ore"));

        SuperHeatingRecipe.builder()
            .requires(AddonItems.CHAOTIC_RAW_ORE, 8)
            .requires(ModItems.EARTH_CORE_SHARD)
            .result(Items.COPPER_INGOT, 4)
            .result(Items.GOLD_INGOT, 4)
            .result(ModItems.ZINC_INGOT, 4)
            .result(ModItems.TIN_INGOT, 4)
            .result(ModItems.TITANIUM_INGOT, 4)
            .result(ModItems.TUNGSTEN_INGOT, 4)
            .result(ModItems.LEAD_INGOT, 4)
            .result(ModItems.SILVER_INGOT, 4)
            .save(provider, AnvilCraftPigsPlus.of("super_heating/chaotic_raw_ore_and_earth_core_shard"));
    }
}
