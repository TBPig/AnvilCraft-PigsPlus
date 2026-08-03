package dev.anvilcraft.pigsplus.client.markdown.recipe;

import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.requirement.ReformerRequirement;
import dev.anvilcraft.pigsplus.api.requirement.RequirementEntry;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe.LaserType;
import dev.anvilcraft.pigsplus.util.ReformerIcons;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.MDRenderContext;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.component.extend.MDRecipeComponent;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.util.AgeratumUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class MDCelestialReformerRecipeComponent extends MDRecipeComponent {
    private static final ResourceLocation TEXTURE = AnvilCraft.of("textures/gui/ageratum/128back.png");
    private static final int WIDTH = 128;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = SLOT_SIZE + 1;
    private static final int SLOTS_PER_ROW = 6;

    private final CelestialReformerRecipe recipe;

    public MDCelestialReformerRecipeComponent(
        CelestialReformerRecipe recipe,
        boolean enableAlignCenter
    ) {
        super(TEXTURE, WIDTH, componentHeight(recipe), enableAlignCenter);
        this.recipe = recipe;
    }

    @Override
    protected void renderRecipe(MDRenderContext context, float mouseX, float mouseY) {
        int slotIndex = 0;
        for (CelestialReformerRecipe.ItemInput input : recipe.items()) {
            var item = BuiltInRegistries.ITEM.get(input.item());
            if (item == Items.AIR) continue;
            this.renderItemSlot(
                context,
                item,
                input.count(),
                getSlotX(slotIndex),
                getSlotY(slotIndex),
                mouseX,
                mouseY
            );
            slotIndex++;
        }
        for (CelestialReformerRecipe.FluidInput input : recipe.fluids()) {
            Fluid fluid = BuiltInRegistries.FLUID.get(input.fluid());
            if (fluid.isSame(Fluids.EMPTY)) continue;
            this.renderFluidSlot(
                context,
                fluid,
                input.amount(),
                getSlotX(slotIndex),
                getSlotY(slotIndex),
                mouseX,
                mouseY
            );
            slotIndex++;
        }

        int iconY = getIconsY(recipe);
        int iconIndex = 0;
        for (RequirementEntry entry : recipe.requirements()) {
            ReformerRequirement requirement = entry.requirement();
            this.renderIconSlot(
                context,
                requirement.getIcon(),
                requirement.getDescription(),
                getIconX(iconIndex),
                iconY,
                mouseX,
                mouseY
            );
            iconIndex++;
        }
        for (CelestialReformerRecipe.LaserInput laser : recipe.lasers()) {
            this.renderIconSlot(
                context,
                ReformerIcons.laserIcon(),
                Component.translatable(
                    "gui.anvilcraft_pigsplus.jei.laser",
                    laser.level(),
                    laserType(laser.type())
                ),
                getIconX(iconIndex),
                iconY,
                mouseX,
                mouseY
            );
            iconIndex++;
        }

        ReformerModification modification = recipe.modification().resolved();
        this.renderIconSlot(
            context,
            modification == null ? ReformerIcons.DEFAULT : modification.getIcon(),
            modification == null
            ? Component.translatable("modification.anvilcraft_pigsplus.unknown")
            : modification.getDescription(),
            getModificationX(),
            getSlotY(0),
            mouseX,
            mouseY
        );
    }

    private void renderItemSlot(
        MDRenderContext context,
        Item item,
        int count,
        int slotX,
        int slotY,
        float mouseX,
        float mouseY
    ) {
        int itemX = slotX + 1;
        int itemY = slotY + 1;
        AgeratumUtil.renderText(
            context.graphics(),
            Component.literal(String.valueOf(count)),
            itemX,
            itemY + 15
        );
        AgeratumUtil.renderItem(context, new ItemStack(item, 1), mouseX, mouseY, itemX, itemY);
    }

    private void renderFluidSlot(
        MDRenderContext context,
        Fluid fluid,
        int amount,
        int slotX,
        int slotY,
        float mouseX,
        float mouseY
    ) {
        context.graphics().blit(
            AgeratumUtil.SLOT,
            slotX - 7,
            slotY - 7,
            0,
            0,
            32,
            32,
            32,
            32
        );
        AgeratumUtil.renderText(
            context.graphics(),
            fluid.getFluidType().getDescription(),
            slotX + 1,
            slotY + 3,
            0.5f
        );
        AgeratumUtil.renderText(
            context.graphics(),
            Component.literal(String.valueOf(amount)),
            slotX + 1,
            slotY + 9,
            0.6f
        );
        if (AgeratumUtil.isHover(
            slotX,
            slotY,
            SLOT_SIZE,
            SLOT_SIZE,
            mouseX,
            mouseY
        )) {
            context.addTooltip(Component.translatable(
                "gui.anvilcraft_pigsplus.jei.fluid",
                fluid.getFluidType().getDescription(),
                amount
            ));
        }
    }

    private void renderIconSlot(
        MDRenderContext context,
        ResourceLocation icon,
        Component tooltip,
        int x,
        int y,
        float mouseX,
        float mouseY
    ) {
        context.graphics().blit(AgeratumUtil.SLOT, x - 7, y - 7, 0, 0, 32, 32, 32, 32);
        context.graphics().blit(
            icon,
            x + 1,
            y + 1,
            0,
            0,
            ReformerIcons.ICON_SIZE,
            ReformerIcons.ICON_SIZE,
            ReformerIcons.ICON_SIZE,
            ReformerIcons.ICON_SIZE
        );
        if (AgeratumUtil.isHover(
            x,
            y,
            ReformerIcons.SLOT_SIZE,
            ReformerIcons.SLOT_SIZE,
            mouseX,
            mouseY
        )) {
            context.addTooltip(tooltip);
        }
    }

    private static int componentHeight(CelestialReformerRecipe recipe) {
        int rowCount = (inputSlotCount(recipe) + SLOTS_PER_ROW - 1) / SLOTS_PER_ROW;
        int iconY = 12 + Math.max(1, rowCount) * SLOT_SPACING + 4;
        return Math.max(64, iconY + SLOT_SIZE + 12);
    }

    private static int getSlotX(int index) {
        return 12 + (index % SLOTS_PER_ROW) * SLOT_SPACING;
    }

    private static int getSlotY(int index) {
        return 12 + (index / SLOTS_PER_ROW) * SLOT_SPACING;
    }

    private static int getIconX(int index) {
        return 12 + index * SLOT_SPACING;
    }

    private static int getModificationX() {
        return WIDTH - SLOT_SIZE - 11;
    }

    private static int getIconsY(CelestialReformerRecipe recipe) {
        int rowCount = (inputSlotCount(recipe) + SLOTS_PER_ROW - 1) / SLOTS_PER_ROW;
        return 12 + Math.max(1, rowCount) * SLOT_SPACING + 4;
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
