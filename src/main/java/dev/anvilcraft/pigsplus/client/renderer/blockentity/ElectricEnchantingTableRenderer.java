package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.BaseShowItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ElectricEnchantingTableRenderer extends BaseShowItemRenderer<ElectricEnchantingTableBlockEntity> {
    public ElectricEnchantingTableRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ItemStack getDisplayItemStack(ElectricEnchantingTableBlockEntity blockEntity) {
        return blockEntity.getDisplayItemStack();
    }

    @Override
    protected int getSeed(ElectricEnchantingTableBlockEntity blockEntity) {
        return 0;
    }

    public void render(
        ElectricEnchantingTableBlockEntity be,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        int packedOverlay
    ) {
        Level level = be.getLevel();
        ItemStack stack = getDisplayItemStack(be);
        if (level == null || stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.0, 0.5);
        poseStack.mulPose(Axis.YP.rotation((level.getGameTime() + partialTick) * 0.02F));
        poseStack.scale(0.4F, 0.4F, 0.4F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
            stack,
            ItemDisplayContext.FIXED,
            packedLight,
            packedOverlay,
            poseStack,
            buffer,
            level,
            getSeed(be)
        );
        poseStack.popPose();
    }
}
