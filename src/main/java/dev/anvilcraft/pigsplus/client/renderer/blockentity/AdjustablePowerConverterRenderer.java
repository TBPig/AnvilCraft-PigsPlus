package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import dev.anvilcraft.pigsplus.block.entity.AdjustablePowerConverterBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.PowerProducerRenderer;
import dev.dubhe.anvilcraft.client.renderer.blockentity.state.PowerGeneratorRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public class AdjustablePowerConverterRenderer
    extends PowerProducerRenderer<AdjustablePowerConverterBlockEntity, PowerGeneratorRenderState> {
    public static final StandaloneModelKey<BlockStateModel> MODEL = new StandaloneModelKey<>(
        () -> "AnvilCraft-PigsPlus: Adjustable Power Converter Cube Model"
    );

    public AdjustablePowerConverterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected float elevation() {
        return 0.5f;
    }

    @Override
    protected StandaloneModelKey<BlockStateModel> getModel() {
        return MODEL;
    }

    @Override
    public PowerGeneratorRenderState createRenderState() {
        return new PowerGeneratorRenderState();
    }
}
