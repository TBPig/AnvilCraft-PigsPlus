package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import dev.anvilcraft.pigsplus.block.entity.EnchantedGeneratorBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.PowerProducerRenderer;
import dev.dubhe.anvilcraft.client.renderer.blockentity.state.PowerGeneratorRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public class EnchantedGeneratorRenderer extends PowerProducerRenderer<EnchantedGeneratorBlockEntity, PowerGeneratorRenderState> {
    public static final StandaloneModelKey<BlockStateModel> MODEL = new StandaloneModelKey<>(
        () -> "AnvilCraft-PigsPlus: Enchanted Generator Cube Model"
    );

    public EnchantedGeneratorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public PowerGeneratorRenderState createRenderState() {
        return new PowerGeneratorRenderState();
    }

    @Override
    protected float elevation() {
        return 0.75f;
    }

    @Override
    protected float rotation(EnchantedGeneratorBlockEntity blockEntity, float partialTick) {
        return blockEntity.getRotation() + blockEntity.getServerPower() * EnchantedGeneratorBlockEntity.ROTATION_PRE_POWER * partialTick;
    }

    @Override
    protected StandaloneModelKey<BlockStateModel> getModel() {
        return MODEL;
    }
}
