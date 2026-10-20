package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import dev.anvilcraft.pigsplus.block.BeamHeatConverterBlock;
import dev.anvilcraft.pigsplus.block.PecisionMagneticPivotBlock;
import dev.anvilcraft.pigsplus.block.SoulboundCampfireBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.block.state.IrradiatorType;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.recipe.anvil.procedural.ProceduralProcessRecipeBuilder;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.BlockProcessingRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemInjectRecipe;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ProceduralProcessRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        // 时空超算
        ProceduralProcessRecipeBuilder.of(ModBlocks.ADVANCED_COMPARATOR.get())
            .addStep(
                fakePrecisionElectromagneticProcessing(ModBlocks.ADVANCED_COMPARATOR.get())
                    .result(ModBlocks.WIP_BLOCK.get())
                    .buildRecipe()
            )
            .addStep(
                BlockProcessingRecipe.builder()
                    .fakeNeutronIrradiation(ModBlocks.WIP_BLOCK.get(), IrradiatorType.TIME)
                    .result(ModBlocks.WIP_BLOCK.get())
                    .buildRecipe()
            )
            .addStep(
                BlockProcessingRecipe.builder()
                    .fakeNeutronIrradiation(ModBlocks.WIP_BLOCK.get(), IrradiatorType.SPACE)
                    .result(ModBlocks.WIP_BLOCK.get())
                    .buildRecipe()
            )
            .result(ModBlocks.SPACETIME_SUPERCOMPUTER)
            .icon(ModBlocks.SPACETIME_SUPERCOMPUTER.asStack())
            .displayedModels(
                AnvilCraft.of("spacetime_supercomputer_wip"),
                AnvilCraft.of("spacetime_supercomputer_wip_2"),
                AnvilCraft.of("spacetime_supercomputer_wip_3")
            )
            .save(provider, "spacetime_supercomputer_from_advanced_comparator_with_precision_magnetic_pivot");

        // 逻辑合金块
        ProceduralProcessRecipeBuilder.of(ModBlocks.TUNGSTEN_BLOCK.get())
            .addStep(
                fakePrecisionElectromagneticProcessing(ModBlocks.TUNGSTEN_BLOCK.get())
                    .result(ModBlocks.WIP_BLOCK.get())
                    .buildRecipe()
            )
            .addStep(
                ItemInjectRecipe.builder()
                    .inputBlock(ModBlocks.WIP_BLOCK.get())
                    .requires(AddonItems.KARAKURI_COMPONENT.get())
                    .resultBlock(ModBlocks.WIP_BLOCK.get())
                    .buildRecipe()
            )
            .addStep(
                fakeSoulVineGrowthProcessing(ModBlocks.WIP_BLOCK.get())
                    .result(ModBlocks.WIP_BLOCK.get())
                    .buildRecipe()
            )
            .addStep(
                ItemInjectRecipe.builder()
                    .inputBlock(ModBlocks.WIP_BLOCK.get())
                    .requires(AddonItems.KARAKURI_COMPONENT.get())
                    .resultBlock(ModBlocks.WIP_BLOCK.get())
                    .buildRecipe()
            )
            .addStep(
                fakeBeamCastingProcessing(ModBlocks.WIP_BLOCK.get())
                    .result(ModBlocks.WIP_BLOCK.get())
                    .buildRecipe()
            )
            .result(AddonBlocks.LOGIC_ALLOY_BLOCK)
            .icon(AddonBlocks.LOGIC_ALLOY_BLOCK.asStack())
            .save(provider);
    }

    public static BlockProcessingRecipe.@NotNull Builder fakePrecisionElectromagneticProcessing(Block input) {
        return BlockProcessingRecipe.builder()
            .input(BlockStatePredicate.builder().of(input).build())
            .input(BlockStatePredicate.builder()
                .of(AddonBlocks.PRECISION_MAGNETIC_PIVOT.get())
                .with(PecisionMagneticPivotBlock.LIT, true)
                .build());
    }

    public static BlockProcessingRecipe.@NotNull Builder fakeSoulVineGrowthProcessing(Block input) {
        return BlockProcessingRecipe.builder()
            .input(BlockStatePredicate.builder().of(input).build())
            .input(BlockStatePredicate.builder()
                .of(AddonBlocks.SOULBOUND_CAMPFIRE.get())
                .with(SoulboundCampfireBlock.LIT, true)
                .build());
    }

    public static BlockProcessingRecipe.@NotNull Builder fakeBeamCastingProcessing(Block input) {
        return BlockProcessingRecipe.builder()
            .input(BlockStatePredicate.builder().of(input).build())
            .input(BlockStatePredicate.builder()
                .of(AddonBlocks.BEAM_HEAT_CONVERTER.get())
                .with(BeamHeatConverterBlock.WORKING, true)
                .build());
    }
}
