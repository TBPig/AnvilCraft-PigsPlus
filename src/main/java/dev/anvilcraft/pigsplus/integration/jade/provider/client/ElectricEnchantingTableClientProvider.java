package dev.anvilcraft.pigsplus.integration.jade.provider.client;

import dev.anvilcraft.lib.v2.util.MathUtil;
import dev.dubhe.anvilcraft.init.block.ModFluids;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.api.view.FluidView;
import snownee.jade.api.view.ProgressView;

import javax.lang.model.element.Element;

import static dev.anvilcraft.pigsplus.integration.jade.provider.ElectricEnchantingTableProvider.UID;

public enum ElectricEnchantingTableClientProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (serverData.contains("decreaseRate")) {
            tooltip.add(Component.translatable(
                "tooltip.anvilcraft_pigsplus.enchanted_generator.power_rate",
                String.format("%.3f", serverData.getDoubleOr("decreaseRate", 0.0))
            ));
        }
        if (serverData.contains("needXpLiquid") && serverData.contains("absorbedXpLiquid")) {
            int consume = serverData.getIntOr("absorbedXpLiquid", 0);
            int generate = serverData.getIntOr("needXpLiquid", 0);
            if (generate != 0) {
                float percent = Mth.clamp(MathUtil.safeDiv(consume, generate), 0, 1);

                tooltip.add(JadeUI.progress(
                    new ProgressView(
                        ProgressView.Part.of(
                            percent,
                            JadeUI.fluid(JadeFluidObject.of(ModFluids.EXP_FLUID.get().getSource()))
                        ),
                        Component.translatable(consume + "/" + generate + " mB"),
                        JadeUI.progressStyle(),
                        BoxStyle.nestedBox()
                    )));
            }
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
