package dev.anvilcraft.pigsplus.integration.jei.category;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.anvilcraft.pigsplus.integration.jei.AddonJeiPlugin;
import dev.anvilcraft.pigsplus.recipe.SoulVineGrowthRecipe;
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

public class SoulVineGrowthCategory extends AbstractLiquidCategory<SoulVineGrowthRecipe> {

    public SoulVineGrowthCategory(IGuiHelper helper) {
        super(
            helper,
            new DrawableBlockStateIcon(
                Blocks.CAULDRON.defaultBlockState(),
                AddonBlocks.SOULBOUND_CAMPFIRE.getDefaultState()
            ),
            Component.translatable("gui.anvilcraft_pigsplus.category.soul_vine_growth")
        );
    }

    @Override
    public RecipeType<RecipeHolder<SoulVineGrowthRecipe>> getRecipeType() {
        return AddonJeiPlugin.SOUL_VINE_GROWTH;
    }

    @Override
    protected BlockState getProcessBlock() {
        return AddonBlocks.SOULBOUND_CAMPFIRE.getDefaultState();
    }

    @Override
    public void getTooltip(
        ITooltipBuilder tooltip,
        RecipeHolder<SoulVineGrowthRecipe> recipeHolder,
        IRecipeSlotsView recipeSlotsView,
        double mouseX,
        double mouseY
    ) {
        if (mouseY >= 34 && mouseY <= 53 && mouseX >= 72 && mouseX <= 90) {
            tooltip.add(AddonBlocks.SOULBOUND_CAMPFIRE.get().getName());
            tooltip.add(Component.translatable(
                "gui.anvilcraft_pigsplus.category.soul_vine_growth.need_working"
            ).withStyle(ChatFormatting.RED));
        }
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        AnvilCraftJeiPlugin.addAnvilCauldronCatalysts(registration, AddonJeiPlugin.SOUL_VINE_GROWTH);
        registration.addRecipeCatalyst(
            new ItemStack(AddonBlocks.SOULBOUND_CAMPFIRE),
            AddonJeiPlugin.SOUL_VINE_GROWTH
        );
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
            AddonJeiPlugin.SOUL_VINE_GROWTH,
            JeiRecipeUtil.getRecipeHoldersFromType(AddonRecipeTypes.SOUL_VINE_GROWTH_TYPE.get())
        );
    }
}
