package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.recipe.PrecisionElectromagneticProcessingRecipe;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class PrecisionElectromagneticProcessingLoader {
    public static void init(RegistrumRecipeProvider provider) {
        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(ModItemTags.BRASS_INGOTS)
            .requires(Items.REDSTONE, 3)
            .result(AddonItems.KARAKURI_COMPONENT)
            .save(provider);

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(ModItems.COPPER_NUGGET, 3)
            .requires(Items.QUARTZ)
            .requires(ModItems.HARDEND_RESIN)
            .result(ModItems.PROCESSOR, 2)
            .save(provider);

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(ModItems.COPPER_NUGGET, 2)
            .requires(Items.REDSTONE)
            .requires(ModItems.HARDEND_RESIN)
            .result(ModItems.CIRCUIT_BOARD, 8)
            .save(provider);

        PrecisionElectromagneticProcessingRecipe.builder()
            .requires(Tags.Items.INGOTS_COPPER, 3)
            .requires(Items.IRON_BLOCK)
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
            .requires(Items.ENDER_PEARL)
            .requires(ModItemTags.MAGNET_INGOTS, 2)
            .result(ModItems.MAGNET, 2)
            .save(provider);
    }
}
