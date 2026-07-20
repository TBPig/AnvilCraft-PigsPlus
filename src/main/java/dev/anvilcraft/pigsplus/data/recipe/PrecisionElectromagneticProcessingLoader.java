package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.recipe.PrecisionElectromagneticProcessingRecipe;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.neoforged.neoforge.common.Tags;

public class PrecisionElectromagneticProcessingLoader {
    public static void init(RegistrumRecipeProvider provider) {
        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(ModItemTags.BRASS_INGOTS)
            .requires(Tags.Items.DUSTS_REDSTONE, 3)
            .result(AddonItems.KARAKURI_COMPONENT)
            .save(provider, AnvilCraftPigsPlus.of("precision_electromagnetic_processing/karakuri_component"));
        PrecisionElectromagneticProcessingRecipe.builder()
            .fluid(AddonBlocks.VOID_ACID_CAULDRON.get())
            .consume(250)
            .requires(ModItemTags.BRASS_INGOTS)
            .requires(Tags.Items.DUSTS_REDSTONE, 3)
            .result(AddonItems.KARAKURI_COMPONENT, 3)
            .save(provider, AnvilCraftPigsPlus.of("precision_electromagnetic_processing/karakuri_component_2"));

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(ModItemTags.COPPER_NUGGETS, 3)
            .requires(Tags.Items.GEMS_QUARTZ)
            .requires(ModItems.HARDEND_RESIN)
            .result(ModItems.PROCESSOR, 2)
            .save(provider, AnvilCraftPigsPlus.of("precision_electromagnetic_processing/processor"));
        PrecisionElectromagneticProcessingRecipe.builder()
            .fluid(AddonBlocks.VOID_ACID_CAULDRON.get())
            .consume(250)
            .requires(ModItemTags.COPPER_NUGGETS, 3)
            .requires(Tags.Items.GEMS_QUARTZ)
            .requires(ModItems.HARDEND_RESIN)
            .result(ModItems.PROCESSOR, 6)
            .save(provider, AnvilCraftPigsPlus.of("precision_electromagnetic_processing/processor_2"));

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(ModItemTags.COPPER_NUGGETS, 2)
            .requires(Tags.Items.DUSTS_REDSTONE)
            .requires(ModItems.HARDEND_RESIN)
            .result(ModItems.CIRCUIT_BOARD, 8)
            .save(provider, AnvilCraftPigsPlus.of("precision_electromagnetic_processing/circuit_board"));
        PrecisionElectromagneticProcessingRecipe.builder()
            .fluid(AddonBlocks.VOID_ACID_CAULDRON.get())
            .consume(250)
            .requires(ModItemTags.COPPER_NUGGETS, 2)
            .requires(Tags.Items.DUSTS_REDSTONE)
            .requires(ModItems.HARDEND_RESIN)
            .result(ModItems.CIRCUIT_BOARD, 24)
            .save(provider, AnvilCraftPigsPlus.of("precision_electromagnetic_processing/circuit_board_2"));

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(Tags.Items.INGOTS_COPPER, 3)
            .requires(ModItemTags.STORAGE_BLOCKS_MAGNET)
            .requires(AddonItems.KARAKURI_COMPONENT)
            .result(AddonBlocks.PRECISION_MAGNETIC_PIVOT)
            .save(provider);

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(Tags.Items.INGOTS_COPPER, 2)
            .requires(Tags.Items.GLASS_BLOCKS, 2)
            .requires(ModItemTags.MAGNET_INGOTS, 3)
            .result(ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK)
            .save(provider);

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(Tags.Items.ENDER_PEARLS)
            .requires(ModItemTags.MAGNET_INGOTS, 2)
            .result(ModItems.MAGNET, 2)
            .save(provider);

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(Tags.Items.STORAGE_BLOCKS_IRON)
            .requires(AddonItems.KARAKURI_COMPONENT)
            .requires(Tags.Items.GEMS_QUARTZ)
            .result(AddonItems.UNIVERSAL_REDSTONE_COMPONENT)
            .save(provider, AnvilCraftPigsPlus.of("precision_electromagnetic_processing/universal_redstone_component"));
        PrecisionElectromagneticProcessingRecipe.builder()
            .fluid(AddonBlocks.VOID_ACID_CAULDRON.get())
            .consume(500)
            .requires(Tags.Items.STORAGE_BLOCKS_IRON)
            .requires(AddonItems.KARAKURI_COMPONENT)
            .requires(Tags.Items.GEMS_QUARTZ)
            .result(AddonItems.UNIVERSAL_REDSTONE_COMPONENT, 4)
            .save(provider, AnvilCraftPigsPlus.of("precision_electromagnetic_processing/universal_redstone_component_2"));
    }
}
