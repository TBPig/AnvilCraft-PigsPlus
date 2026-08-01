package dev.anvilcraft.pigsplus.client.markdown.recipe;

import dev.anvilcraft.pigsplus.api.modification.ReformerModifications;
import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.requirement.RequirementEntry;
import dev.anvilcraft.pigsplus.api.requirement.ReformerRequirement;
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
import net.minecraft.world.item.ItemStack;

public class MDCelestialReformerRecipeComponent extends MDRecipeComponent {
    private static final ResourceLocation TEXTURE = AnvilCraft.of("textures/gui/ageratum/128back.png");

    private final CelestialReformerRecipe recipe;

    public MDCelestialReformerRecipeComponent(
        CelestialReformerRecipe recipe,
        boolean enableAlignCenter
    ) {
        super(TEXTURE, 128, componentHeight(recipe), enableAlignCenter);
        this.recipe = recipe;
    }

    @Override
    protected void renderRecipe(MDRenderContext context, float mouseX, float mouseY) {
        int x = 8;
        for (CelestialReformerRecipe.ItemInput input : recipe.items()) {
            var item = BuiltInRegistries.ITEM.get(input.item());
            ItemStack stack = new ItemStack(item, Math.min(input.count(), 64));
            AgeratumUtil.renderItem(context, stack, mouseX, mouseY, x, 24);
            AgeratumUtil.renderText(
                context.graphics(),
                Component.literal(String.valueOf(input.count())),
                x - 8,
                44
            );
            x += 20;
        }
        AgeratumUtil.renderArrow(context.graphics(), 78, 16);
        int iconY = 56;
        int iconIndex = 0;
        ReformerModification modification =
            ReformerModifications.REGISTRY.get(recipe.modification());
        if (modification != null) {
            this.renderIconSlot(
                context,
                ReformerIcons.SLOT_CONCEPT,
                modification.getIcon(),
                modification.getDescription(),
                iconY,
                iconIndex,
                mouseX,
                mouseY
            );
            iconIndex++;
        }
        for (RequirementEntry entry : recipe.requirements()) {
            ReformerRequirement requirement = entry.requirement();
            this.renderIconSlot(
                context,
                ReformerIcons.SLOT_CONCEPT,
                ReformerIcons.requirementIcon(entry.id()),
                requirement.getDescription(),
                iconY,
                iconIndex,
                mouseX,
                mouseY
            );
            iconIndex++;
        }
        for (CelestialReformerRecipe.LaserInput laser : recipe.lasers()) {
            this.renderIconSlot(
                context,
                ReformerIcons.SLOT_CONCEPT,
                ReformerIcons.laserIcon(),
                Component.translatable(
                    "gui.anvilcraft_pigsplus.jei.laser",
                    laser.level(),
                    laserType(laser.type())
                ),
                iconY,
                iconIndex,
                mouseX,
                mouseY
            );
            iconIndex++;
        }

        int infoY = iconY + ((iconIndex + 5) / 6) * ReformerIcons.SLOT_SIZE + 8;
        for (CelestialReformerRecipe.FluidInput input : recipe.fluids()) {
            var fluid = BuiltInRegistries.FLUID.get(input.fluid());
            AgeratumUtil.renderText(
                context.graphics(),
                Component.translatable(
                    "gui.anvilcraft_pigsplus.jei.fluid",
                    fluid.getFluidType().getDescription(),
                    input.amount()
                ),
                8,
                infoY
            );
            infoY += 10;
        }
    }

    private void renderIconSlot(
        MDRenderContext context,
        net.minecraft.resources.ResourceLocation slot,
        net.minecraft.resources.ResourceLocation icon,
        Component tooltip,
        int startY,
        int index,
        float mouseX,
        float mouseY
    ) {
        int x = 8 + (index % 6) * ReformerIcons.SLOT_SIZE;
        int y = startY + (index / 6) * ReformerIcons.SLOT_SIZE;
        context.graphics().blit(
            slot,
            x,
            y,
            0,
            0,
            ReformerIcons.SLOT_SIZE,
            ReformerIcons.SLOT_SIZE,
            ReformerIcons.SLOT_SIZE,
            ReformerIcons.SLOT_SIZE
        );
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
        int iconCount = 1 + recipe.requirements().size() + recipe.lasers().size();
        int rows = Math.max(1, (iconCount + 5) / 6);
        return Math.max(96, 56 + rows * ReformerIcons.SLOT_SIZE + 20);
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
