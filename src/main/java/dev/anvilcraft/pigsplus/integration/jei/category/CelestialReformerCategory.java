package dev.anvilcraft.pigsplus.integration.jei.category;

import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.modification.ReformerModifications;
import dev.anvilcraft.pigsplus.api.requirement.RequirementEntry;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.anvilcraft.pigsplus.integration.jei.AddonJeiPlugin;
import dev.anvilcraft.pigsplus.integration.jei.ingredient.ReformerConcept;
import dev.anvilcraft.pigsplus.integration.jei.util.AddonJeiUtil;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe.LaserType;
import dev.anvilcraft.pigsplus.util.ReformerIcons;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.integration.jei.util.JeiRecipeUtil;
import dev.dubhe.anvilcraft.integration.jei.util.JeiRenderHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class CelestialReformerCategory implements IRecipeCategory<RecipeHolder<CelestialReformerRecipe>> {
    private static final int WIDTH = 162;
    private static final int HEIGHT = 64;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = SLOT_SIZE + 1;
    // 162px 宽度下可容纳 6 列槽位
    private static final int SLOTS_PER_ROW = 6;

    private final IDrawable icon;
    private final IDrawable slotDefault;
    private final IDrawable conceptSlot;
    private final Component title;

    public CelestialReformerCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemLike(ModBlocks.CELESTIAL_FORGING_ANVIL.asItem());
        this.slotDefault = JeiRenderHelper.getSlotDefault(helper);
        this.conceptSlot = helper.drawableBuilder(
            ReformerIcons.SLOT_CONCEPT,
            0,
            0,
            ReformerIcons.SLOT_SIZE,
            ReformerIcons.SLOT_SIZE
        ).setTextureSize(ReformerIcons.SLOT_SIZE, ReformerIcons.SLOT_SIZE).build();
        this.title = Component.translatable("gui.anvilcraft_pigsplus.category.celestial_reformer");
    }

    @Override
    public RecipeType<RecipeHolder<CelestialReformerRecipe>> getRecipeType() {
        return AddonJeiPlugin.CELESTIAL_REFORMER;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(
        IRecipeLayoutBuilder builder,
        RecipeHolder<CelestialReformerRecipe> recipeHolder,
        IFocusGroup focuses
    ) {
        CelestialReformerRecipe recipe = recipeHolder.value();
        int slotIndex = 0;
        for (CelestialReformerRecipe.ItemInput input : recipe.items()) {
            var item = BuiltInRegistries.ITEM.get(input.item());
            if (item == Items.AIR) continue;
            AddonJeiUtil.addItemInputSlot(
                builder,
                getSlotX(slotIndex),
                getSlotY(slotIndex),
                item,
                input.count()
            );
            slotIndex++;
        }
        for (CelestialReformerRecipe.FluidInput input : recipe.fluids()) {
            Fluid fluid = BuiltInRegistries.FLUID.get(input.fluid());
            if (fluid.isSame(Fluids.EMPTY)) continue;
            AddonJeiUtil.addFluidInputSlot(
                builder,
                getSlotX(slotIndex),
                getSlotY(slotIndex),
                fluid,
                input.amount()
            );
            slotIndex++;
        }

        int iconY = getIconsY(recipe);
        int iconIndex = 0;
        for (RequirementEntry entry : recipe.requirements()) {
            var requirement = entry.requirement();
            if (requirement != null) {
                this.addConceptSlot(
                    builder,
                    RecipeIngredientRole.INPUT,
                    new ReformerConcept(entry.requirement().getIcon()),
                    requirement.getDescription(),
                    getIconX(iconIndex),
                    iconY
                );
            }
            iconIndex++;
        }
        for (CelestialReformerRecipe.LaserInput laser : recipe.lasers()) {
            this.addConceptSlot(
                builder,
                RecipeIngredientRole.INPUT,
                new ReformerConcept(ReformerIcons.laserIcon()),
                Component.translatable(
                    "gui.anvilcraft_pigsplus.jei.laser",
                    laser.level(),
                    laserType(laser.type())
                ),
                getIconX(iconIndex),
                iconY
            );
            iconIndex++;
        }

        ReformerModification modification =
            ReformerModifications.REGISTRY.get(recipe.modification());
        ReformerConcept outputConcept = modification == null
            ? new ReformerConcept(ReformerIcons.DEFAULT)
            : new ReformerConcept(modification.getIcon());
        Component outputTooltip = modification == null
            ? Component.translatable("modification.anvilcraft_pigsplus.unknown")
            : modification.getDescription();
        this.addConceptSlot(
            builder,
            RecipeIngredientRole.OUTPUT,
            outputConcept,
            outputTooltip,
            getModificationX(),
            getSlotY(0)
        );
    }

    @Override
    public void draw(
        RecipeHolder<CelestialReformerRecipe> recipeHolder,
        IRecipeSlotsView recipeSlotsView,
        GuiGraphics guiGraphics,
        double mouseX,
        double mouseY
    ) {
        CelestialReformerRecipe recipe = recipeHolder.value();
        for (int i = 0; i < inputSlotCount(recipe); i++) {
            slotDefault.draw(guiGraphics, getSlotX(i) - 1, getSlotY(i) - 1);
        }
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
            AddonJeiPlugin.CELESTIAL_REFORMER,
            JeiRecipeUtil.getRecipeHoldersFromType(AddonRecipeTypes.CELESTIAL_REFORMER_TYPE.get())
        );
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.CELESTIAL_FORGING_ANVIL, AddonJeiPlugin.CELESTIAL_REFORMER);
        registration.addRecipeCatalyst(ModBlocks.CELESTIAL_FORGING_ANVIL_LOGISTICS_INTERFACE, AddonJeiPlugin.CELESTIAL_REFORMER);
        registration.addRecipeCatalyst(ModBlocks.CELESTIAL_FORGING_ANVIL_FLUID_INTERFACE, AddonJeiPlugin.CELESTIAL_REFORMER);
        registration.addRecipeCatalyst(ModBlocks.CELESTIAL_FORGING_ANVIL_LASER_INTERFACE, AddonJeiPlugin.CELESTIAL_REFORMER);
        registration.addRecipeCatalyst(ModBlocks.CELESTIAL_FORGING_ANVIL_AMPLIFIER, AddonJeiPlugin.CELESTIAL_REFORMER);
        registration.addRecipeCatalyst(AddonItems.CELESTIAL_REFORMER_COMPONENT, AddonJeiPlugin.CELESTIAL_REFORMER);
    }

    private static int getSlotX(int index) {
        return 8 + (index % SLOTS_PER_ROW) * SLOT_SPACING;
    }

    private static int getSlotY(int index) {
        return 8 + (index / SLOTS_PER_ROW) * SLOT_SPACING;
    }

    private static int getIconX(int index) {
        return 8 + index * SLOT_SPACING;
    }

    private static int getModificationX() {
        return WIDTH - SLOT_SIZE;
    }

    private static int getIconsY(CelestialReformerRecipe recipe) {
        int rowCount = (inputSlotCount(recipe) + SLOTS_PER_ROW - 1) / SLOTS_PER_ROW;
        return 8 + Math.max(1, rowCount) * SLOT_SPACING + 4;
    }

    private void addConceptSlot(
        IRecipeLayoutBuilder builder,
        RecipeIngredientRole role,
        ReformerConcept concept,
        Component tooltip,
        int x,
        int y
    ) {
        builder.addSlot(role, x, y)
            .setBackground(this.conceptSlot, -1, -1)
            .addIngredient(ReformerConcept.TYPE, concept)
            .addRichTooltipCallback((slotView, tooltipBuilder) -> tooltipBuilder.add(tooltip));
    }

    private static int inputSlotCount(CelestialReformerRecipe recipe) {
        return recipe.items().size() + recipe.fluids().size();
    }

    private static Component laserType(LaserType type) {
        String key = type == LaserType.ANY
                     ? "gui.anvilcraft_pigsplus.laser.type.any"
                     : type == LaserType.GAMMA
                       ? "gui.anvilcraft_pigsplus.laser.type.gamma"
                       : "gui.anvilcraft_pigsplus.laser.type.normal";
        return Component.translatable(key);
    }
}
