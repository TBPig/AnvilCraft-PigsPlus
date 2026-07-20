package dev.anvilcraft.pigsplus.client.markdown.recipe;

import dev.anvilcraft.lib.v2.util.predicate.ChanceItemStack;
import dev.anvilcraft.lib.v2.util.predicate.ItemIngredientPredicate;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.recipe.PrecisionElectromagneticProcessingRecipe;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.MDRenderContext;
import dev.dubhe.anvilcraft.client.markdown.recipe.anvil.MDBaseAnvilRecipeComponent;
import dev.dubhe.anvilcraft.util.AgeratumUtil;
import dev.dubhe.anvilcraft.util.CauldronUtil;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MDPrecisionElectromagneticProcessingRecipeComponent extends MDBaseAnvilRecipeComponent {
    public static final int INFO_X = 12;
    public static final int INFO_Y = 106;
    @Getter
    private final List<ItemIngredientPredicate> ingredients;

    @Getter
    private final List<ChanceItemStack> resultItems;

    @Getter
    private final List<BlockState> inputBlockStates;

    @Getter
    private final PrecisionElectromagneticProcessingRecipe recipe;

    public MDPrecisionElectromagneticProcessingRecipeComponent(PrecisionElectromagneticProcessingRecipe recipe, boolean enableAlignCenter) {
        super(enableAlignCenter);
        ingredients = recipe.getInputItems();
        resultItems = recipe.getResultItems();
        inputBlockStates = List.of(
            getInputCauldron(recipe),
            AddonBlocks.PRECISION_MAGNETIC_PIVOT.getDefaultState()
        );
        this.recipe = recipe;
    }

    protected BlockState getOutputBlockState() {
        if (resultItems.isEmpty()) {
            return getResultCauldron(recipe);
        }
        return super.getOutputBlockState();
    }

    @Override
    protected void renderRecipe(MDRenderContext context, float mouseX, float mouseY) {
        super.renderRecipe(context, mouseX, mouseY);
        GuiGraphics graphics = context.graphics();

        if (recipe.isConsumeFluid()) {
            Component text = Component.translatable(
                "gui.anvilcraft_pigsplus.category.precision_electromagnetic_processing.consume_fluid",
                recipe.getHasCauldron().consume(),
                recipe.getHasCauldron().getFluidCauldron().getName()
            );
            AgeratumUtil.renderText(graphics, text, INFO_X, INFO_Y);
        } else if (recipe.isProduceFluid()) {
            Component text = Component.translatable(
                "gui.anvilcraft_pigsplus.category.precision_electromagnetic_processing.produce_fluid",
                recipe.getHasCauldron().produce(),
                recipe.getHasCauldron().getTransformCauldron().getName()
            );
            AgeratumUtil.renderText(graphics, text, INFO_X, INFO_Y);
        }
    }

    public static BlockState getInputCauldron(PrecisionElectromagneticProcessingRecipe recipe) {
        Block material = recipe.getHasCauldron().getFluidCauldron();
        return CauldronUtil.fullState(material);
    }

    public static BlockState getResultCauldron(PrecisionElectromagneticProcessingRecipe recipe) {
        Block result = recipe.getHasCauldron().getTransformCauldron();
        if (recipe.isConsumeFluid()) {
            return CauldronUtil.getStateFromContentAndLevel(result, CauldronUtil.maxLevel(result) - 1);
        } else if (recipe.isProduceFluid()) {
            return CauldronUtil.getStateFromContentAndLevel(result, 1);
        } else {
            return CauldronUtil.fullState(result);
        }
    }
}
