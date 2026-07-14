package dev.anvilcraft.pigsplus.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.entity.StalkerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Axis;

import static dev.anvilcraft.pigsplus.entity.StalkerEntity.SEGMENTS_PER_TENTACLE;
import static dev.anvilcraft.pigsplus.entity.StalkerEntity.TENTACLE_NUM;

public class StalkerModel extends EntityModel<StalkerEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
        new ModelLayerLocation(AnvilCraftPigsPlus.of("stalker"), "main");

    private final ModelPart body;
    private final ModelPart segmentTemplate;

    private final Vec3[][] segmentPos = new Vec3[TENTACLE_NUM][SEGMENTS_PER_TENTACLE];
    private final boolean[][] segmentAlive = new boolean[TENTACLE_NUM][SEGMENTS_PER_TENTACLE];
    private float invAngle;

    public StalkerModel(ModelPart root) {
        this.body = root.getChild("body");
        this.segmentTemplate = root.getChild("tentacle_segment");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition partDefinition = mesh.getRoot();
        partDefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 16)
                .addBox(-6.0F, -12.0F, -4.0F, 12.0F, 16.0F, 8.0F),
            PartPose.offset(0.0F, 4.0F, 0.0F)
        );
        partDefinition.addOrReplaceChild(
            "tentacle_segment",
            CubeListBuilder.create().texOffs(0, 48)
                .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F),
            PartPose.ZERO
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(
        StalkerEntity entity,
        float limbSwing,
        float limbSwingAmount,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        // Interpolated body yaw matching the renderer's value used in setupRotations
        float partialTick = ageInTicks - entity.tickCount;
        float bodyRot = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        this.invAngle = (bodyRot - 180) * Mth.DEG_TO_RAD;

        Vec3 targetDir = entity.getTarget() != null
            ? entity.getTarget().position().subtract(entity.position()) : null;
        float attackProgress = entity.swinging
            ? Math.max(0, entity.swingTime / 10.0F) : 0;

        for (int t = 0; t < TENTACLE_NUM; t++) {
            for (int s = 0; s < SEGMENTS_PER_TENTACLE; s++) {
                float scale = entity.getSegmentScale(t);
                this.segmentAlive[t][s] = scale > 0.01F;
                if (!this.segmentAlive[t][s]) continue;

                Vec3 worldOffset = StalkerEntity.getTentacleOffset(
                    t, s, ageInTicks,
                    targetDir, entity.getYRot(), attackProgress, entity.getTentacleHurtTime(t));
                // y 偏离 -1.1 对齐碰撞箱，我也不知道为啥对不齐
                this.segmentPos[t][s] = worldOffset.yRot(invAngle).add(new Vec3(0, -1.1, 0));
            }
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.body.render(poseStack, buffer, packedLight, packedOverlay, color);

        for (int t = 0; t < TENTACLE_NUM; t++) {
            for (int s = 0; s < SEGMENTS_PER_TENTACLE; s++) {
                if (!this.segmentAlive[t][s]) continue;

                poseStack.pushPose();
                poseStack.translate(-this.segmentPos[t][s].x, -this.segmentPos[t][s].y, this.segmentPos[t][s].z);
                poseStack.mulPose(Axis.YP.rotation(-this.invAngle));
                this.segmentTemplate.render(poseStack, buffer, packedLight, packedOverlay, color);
                poseStack.popPose();
            }
        }
    }
}
