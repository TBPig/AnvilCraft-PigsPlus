package dev.anvilcraft.pigsplus.client.markdown.recipe;

import dev.anvilcraft.lib.v2.util.predicate.ChanceItemStack;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.pigsplus.block.BeamHeatConverterBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.recipe.BeamCastingRecipe;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.MDRenderContext;
import dev.dubhe.anvilcraft.client.markdown.recipe.anvil.MDBaseAnvilRecipeComponent;
import dev.dubhe.anvilcraft.recipe.anvil.predicate.block.HasCauldron;
import dev.dubhe.anvilcraft.util.AgeratumUtil;
import dev.dubhe.anvilcraft.util.CauldronUtil;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class MDBeamCastingRecipeComponent extends MDBaseAnvilRecipeComponent {
    public static final int INFO_X = 12;
    public static final int INFO_Y = 106;
    @Getter
    private final List<ItemIngredientPredicate> ingredients;

    @Getter
    private final List<ChanceItemStack> resultItems;

    @Getter
    private final List<BlockState> inputBlockStates;

    @Getter
    private final BeamCastingRecipe recipe;

    public MDBeamCastingRecipeComponent(BeamCastingRecipe recipe, boolean enableAlignCenter) {
        super(enableAlignCenter);
        ingredients = recipe.getInputItems();
        resultItems = recipe.getResultItems();
        inputBlockStates = List.of(
            getInputCauldron(recipe),
            AddonBlocks.BEAM_HEAT_CONVERTER.getDefaultState().setValue(BeamHeatConverterBlock.WORKING, true)
        );
        this.recipe = recipe;
    }

    @Override
    protected BlockState getOutputBlockState() {
        if (this.resultItems.isEmpty()) {
            return getResultCauldron(this.recipe);
        }
        return super.getOutputBlockState();
    }

    @Override
    protected void renderRecipe(MDRenderContext context, float mouseX, float mouseY) {
        super.renderRecipe(context, mouseX, mouseY);
        GuiGraphics graphics = context.graphics();

        if (this.recipe.isConsumeFluid()) {
            Component text = Component.translatable(
                "gui.anvilcraft_pigsplus.category.consume_fluid",
                this.recipe.getHasCauldron().consume(),
                HasCauldron.getDefaultCauldron(this.recipe.getHasCauldron().fluid()).getName()
            );
            AgeratumUtil.renderText(graphics, text, INFO_X, INFO_Y);
        } else if (this.recipe.isProduceFluid()) {
            FluidStack transform = getDisplayedElement(this.recipe.getHasCauldron().transforms());
            Component text = Component.translatable(
                "gui.anvilcraft_pigsplus.category.produce_fluid",
                transform.getAmount(),
                HasCauldron.getDefaultCauldron(transform.getFluid()).getName()
            );
            AgeratumUtil.renderText(graphics, text, INFO_X, INFO_Y);
        }
    }

    public static BlockState getInputCauldron(BeamCastingRecipe recipe) {
        Block material = HasCauldron.getDefaultCauldron(recipe.getHasCauldron().fluid());
        return CauldronUtil.fullState(material);
    }

    public static BlockState getResultCauldron(BeamCastingRecipe recipe) {
        List<FluidStack> transforms = recipe.getHasCauldron().transforms();
        Block result = transforms.isEmpty()
                       ? HasCauldron.getDefaultCauldron(recipe.getHasCauldron().fluid())
                       : HasCauldron.getDefaultCauldron(getDisplayedElement(transforms).getFluid());
        if (recipe.isConsumeFluid()) {
            return CauldronUtil.getStateFromContentAndLevel(result, CauldronUtil.maxLevel(result) - 1);
        } else if (recipe.isProduceFluid()) {
            return CauldronUtil.getStateFromContentAndLevel(result, 1);
        } else {
            return CauldronUtil.fullState(result);
        }
    }
}
