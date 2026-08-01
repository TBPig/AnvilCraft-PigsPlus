package dev.anvilcraft.pigsplus.client.markdown.recipe;

import dev.anvilcraft.pigsplus.api.modification.ReformerModifications;
import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.requirement.RequirementEntry;
import dev.anvilcraft.pigsplus.api.requirement.ReformerRequirement;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe.LaserType;
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
        super(TEXTURE, 128, 64, enableAlignCenter);
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
        int textY = 24;
        ReformerModification modification =
            ReformerModifications.REGISTRY.get(recipe.modification());
        if (modification != null) {
            AgeratumUtil.renderText(
                context.graphics(),
                modification.getDescription(),
                100,
                textY
            );
            textY += 10;
        }
        for (RequirementEntry entry : recipe.requirements()) {
            ReformerRequirement requirement = entry.requirement();
            AgeratumUtil.renderText(
                context.graphics(),
                requirement.getDescription(),
                100,
                textY
            );
            textY += 10;
        }

        int infoY = textY + 16;
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
        for (CelestialReformerRecipe.LaserInput input : recipe.lasers()) {
            AgeratumUtil.renderText(
                context.graphics(),
                Component.translatable(
                    "gui.anvilcraft_pigsplus.jei.laser",
                    input.level(),
                    laserType(input.type())
                ),
                8,
                infoY
            );
            infoY += 10;
        }
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
