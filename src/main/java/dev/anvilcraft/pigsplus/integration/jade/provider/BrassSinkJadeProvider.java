package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.view.FluidView;

public enum BrassSinkJadeProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        long volume = Integer.MAX_VALUE;
        JadeFluidObject fluid = JadeFluidObject.of(Fluids.WATER, volume);
        FluidView view = FluidView.readDefault(FluidView.writeDefault(fluid, volume));
        if (view == null || view.fluidName == null) {
            return;
        }

        IElementHelper helper = IElementHelper.get();
        Component text = view.fluidName.copy()
            .append(" ")
            .append(Component.translatable("tooltip.anvilcraft.infinity"))
            .withStyle(ChatFormatting.GRAY);
        tooltip.add(helper.progress(
            1,
            text,
            helper.progressStyle().overlay(view.overlay),
            BoxStyle.getNestedBox(),
            true
        ));
    }

    @Override
    public ResourceLocation getUid() {
        return AnvilCraftPigsPlus.of("brass_sink");
    }
}
