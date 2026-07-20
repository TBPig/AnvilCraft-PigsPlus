package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.util.predicate.BlockStatePredicate;
import dev.anvilcraft.pigsplus.block.PecisionMagneticPivotBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.block.state.IrradiatorType;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.recipe.anvil.procedural.ProceduralProcessRecipeBuilder;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.BlockProcessingRecipe;
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
            .displayedModel(AnvilCraft.of("block/spacetime_supercomputer_wip"))
            .save(provider, "spacetime_supercomputer_from_advanced_comparator_with_precision_magnetic_pivot");
    }

    public static BlockProcessingRecipe.@NotNull Builder fakePrecisionElectromagneticProcessing(Block input) {
        return BlockProcessingRecipe.builder()
            .input(BlockStatePredicate.builder().of(input).build())
            .input(BlockStatePredicate.builder()
                .of(AddonBlocks.PRECISION_MAGNETIC_PIVOT.get())
                .with(PecisionMagneticPivotBlock.LIT, true)
                .build());
    }
}
