package dev.anvilcraft.pigsplus.integration.jei.category;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.anvilcraft.pigsplus.integration.jei.AddonJeiPlugin;
import dev.anvilcraft.pigsplus.recipe.PrecisionElectromagneticProcessingRecipe;
import dev.dubhe.anvilcraft.integration.jei.AnvilCraftJeiPlugin;
import dev.dubhe.anvilcraft.integration.jei.category.anvil.liquid.AbstractLiquidCategory;
import dev.dubhe.anvilcraft.integration.jei.drawable.DrawableBlockStateIcon;
import dev.dubhe.anvilcraft.integration.jei.util.JeiRecipeUtil;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PrecisionElectromagneticProcessingCategory extends AbstractLiquidCategory<PrecisionElectromagneticProcessingRecipe> {

    public PrecisionElectromagneticProcessingCategory(IGuiHelper helper) {
        super(
            helper,
            new DrawableBlockStateIcon(
                Blocks.CAULDRON.defaultBlockState(),
                AddonBlocks.PRECISION_MAGNETIC_PIVOT.getDefaultState()
            ),
            Component.translatable("gui.anvilcraft_pigsplus.category.precision_electromagnetic_processing")
        );
    }

    @Override
    public RecipeType<RecipeHolder<PrecisionElectromagneticProcessingRecipe>> getRecipeType() {
        return AddonJeiPlugin.PRECISION_ELECTROMAGNETIC_PROCESSING;
    }

    @Override
    protected BlockState getProcessBlock() {
        return AddonBlocks.PRECISION_MAGNETIC_PIVOT.getDefaultState();
    }


    @Override
    public void getTooltip(
        ITooltipBuilder tooltip,
        RecipeHolder<PrecisionElectromagneticProcessingRecipe> recipeHolder,
        IRecipeSlotsView recipeSlotsView,
        double mouseX,
        double mouseY
    ) {
        if (mouseY >= 34 && mouseY <= 53 && mouseX >= 72 && mouseX <= 90) {
            tooltip.add(AddonBlocks.PRECISION_MAGNETIC_PIVOT.get().getName());
            tooltip.add(Component.translatable(
                "gui.anvilcraft_pigsplus.category.precision_electromagnetic_processing.need_activated"
            ).withStyle(ChatFormatting.RED));
        }
    }

    public static void registerRecipeCatalyst(IRecipeCatalystRegistration registration) {
        AnvilCraftJeiPlugin.addAnvilCauldronCatalysts(registration, AddonJeiPlugin.PRECISION_ELECTROMAGNETIC_PROCESSING);
        registration.addRecipeCatalyst(
            new ItemStack(AddonBlocks.PRECISION_MAGNETIC_PIVOT),
            AddonJeiPlugin.PRECISION_ELECTROMAGNETIC_PROCESSING
        );
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
            AddonJeiPlugin.PRECISION_ELECTROMAGNETIC_PROCESSING,
            JeiRecipeUtil.getRecipeHoldersFromType(AddonRecipeTypes.PRECISION_ELECTROMAGNETIC_PROCESSING_TYPE.get())
        );
    }
}
