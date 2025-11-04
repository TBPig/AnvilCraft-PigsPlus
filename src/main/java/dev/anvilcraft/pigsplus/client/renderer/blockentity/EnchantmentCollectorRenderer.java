package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.EnchantmentCollectorBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.PowerProducerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class EnchantmentCollectorRenderer extends PowerProducerRenderer<EnchantmentCollectorBlockEntity> {
    public static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(
        AnvilCraftPigsPlus.of("block/enchantment_collector_head")
    );
    public EnchantmentCollectorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected float elevation() {
        return 0.75f;
    }

    @Override
    protected float rotation(EnchantmentCollectorBlockEntity blockEntity, float partialTick) {
        return blockEntity.getRotation() + blockEntity.getServerPower() * EnchantmentCollectorBlockEntity.ROTATION_PRE_POWER * partialTick;
    }

    @Override
    protected ModelResourceLocation getModel() {
        return MODEL;
    }
}
