package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.AdjustablePowerConverterBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.PowerProducerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class AdjustablePowerConverterRenderer extends PowerProducerRenderer<AdjustablePowerConverterBlockEntity> {
    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
        AnvilCraftPigsPlus.of("block/adjustable_power_converter_core")
    );
    public static final ModelResourceLocation MODEL_OUT = ModelResourceLocation.standalone(
        AnvilCraftPigsPlus.of("block/adjustable_power_converter_core_out")
    );

    public AdjustablePowerConverterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected float elevation() {
        return 0.5f;
    }


    @Override
    protected ModelResourceLocation getModel() {
        return MODEL;
    }
}
