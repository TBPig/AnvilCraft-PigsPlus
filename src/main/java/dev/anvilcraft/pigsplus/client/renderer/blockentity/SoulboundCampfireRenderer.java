package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.anvilcraft.pigsplus.block.entity.SoulboundCampfireBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;

/**
 * 缠魂营火的方块实体渲染器
 *
 * <p>复刻原版营火的物品展示逻辑</p>
 */
public class SoulboundCampfireRenderer implements BlockEntityRenderer<SoulboundCampfireBlockEntity> {
    private static final float SIZE = 0.375F;

    private final ItemRenderer itemRenderer;

    public SoulboundCampfireRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
        SoulboundCampfireBlockEntity blockEntity,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        Direction facing = blockEntity.getBlockState().getValue(CampfireBlock.FACING);
        NonNullList<ItemStack> items = blockEntity.getItems();
        int seed = (int) blockEntity.getBlockPos().asLong();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.44921875F, 0.5F);
            Direction itemDirection = Direction.from2DDataValue((i + facing.get2DDataValue()) % 4);
            poseStack.mulPose(Axis.YP.rotationDegrees(-itemDirection.toYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(-0.3125F, -0.3125F, 0.0F);
            poseStack.scale(SIZE, SIZE, SIZE);
            this.itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                seed + i
            );
            poseStack.popPose();
        }
    }
}
