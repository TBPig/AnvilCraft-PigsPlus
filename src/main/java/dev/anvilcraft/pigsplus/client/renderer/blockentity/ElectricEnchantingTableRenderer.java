package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.BaseShowItemRenderer;
import dev.dubhe.anvilcraft.client.support.RenderModelSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class ElectricEnchantingTableRenderer extends BaseShowItemRenderer<ElectricEnchantingTableBlockEntity> {
    public ElectricEnchantingTableRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected ItemStack getDisplayItemStack(ElectricEnchantingTableBlockEntity blockEntity) {
        // 使用从服务端同步过来的显示物品
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
        ItemStack stack = getDisplayItemStack(be);
        if (stack.isEmpty()) return;
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, be.getLevel(), null, getSeed(be));

        AABB aabb = RenderModelSupport.getSize(model);

        double modelDepth = aabb.getZsize();

        double x = 0.5;
        double y = 0.7625 + modelDepth / 4;
        double z = 0.375;

        poseStack.pushPose();

        // 先平移到计算好的位置，再进行旋转
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));

        Minecraft.getInstance()
            .getItemRenderer()
            .render(stack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, model);
        poseStack.popPose();
    }
}
