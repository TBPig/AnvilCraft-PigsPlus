package dev.anvilcraft.pigsplus.integration.jei.category;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.anvilcraft.pigsplus.integration.jei.AddonJeiPlugin;
import dev.anvilcraft.pigsplus.recipe.BeamCastingRecipe;
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

public class BeamCastingCategory extends AbstractLiquidCategory<BeamCastingRecipe> {

    public BeamCastingCategory(IGuiHelper helper) {
        super(
            helper,
            new DrawableBlockStateIcon(
                Blocks.CAULDRON.defaultBlockState(),
                AddonBlocks.BEAM_HEAT_CONVERTER.getDefaultState()
            ),
            Component.translatable("gui.anvilcraft_pigsplus.category.beam_casting")
        );
    }

    @Override
    public RecipeType<RecipeHolder<BeamCastingRecipe>> getRecipeType() {
        return AddonJeiPlugin.BEAM_CASTING;
    }

    @Override
    protected BlockState getProcessBlock() {
        return AddonBlocks.BEAM_HEAT_CONVERTER.getDefaultState();
    }

    @Override
    public void getTooltip(
        ITooltipBuilder tooltip,
        RecipeHolder<BeamCastingRecipe> recipeHolder,
        IRecipeSlotsView recipeSlotsView,
        double mouseX,
        double mouseY
    ) {
        if (mouseY >= 34 && mouseY <= 53 && mouseX >= 72 && mouseX <= 90) {
            tooltip.add(AddonBlocks.BEAM_HEAT_CONVERTER.get().getName());
            tooltip.add(Component.translatable(
                "gui.anvilcraft_pigsplus.category.beam_casting.need_working"
            ).withStyle(ChatFormatting.RED));
        }
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        AnvilCraftJeiPlugin.addAnvilCauldronCatalysts(registration, AddonJeiPlugin.BEAM_CASTING);
        registration.addRecipeCatalyst(
            new ItemStack(AddonBlocks.BEAM_HEAT_CONVERTER),
            AddonJeiPlugin.BEAM_CASTING
        );
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
            AddonJeiPlugin.BEAM_CASTING,
            JeiRecipeUtil.getRecipeHoldersFromType(AddonRecipeTypes.BEAM_CASTING_TYPE.get())
        );
    }
}
