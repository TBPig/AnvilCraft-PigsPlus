package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.anvilcraft.pigsplus.block.entity.ExperienceInterfaceBlockEntity;
import dev.dubhe.anvilcraft.client.renderer.blockentity.state.PowerGeneratorRenderState;
import dev.dubhe.anvilcraft.client.support.FeatureRendererSupport;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jspecify.annotations.Nullable;

public class ExperienceInterfaceRenderer implements BlockEntityRenderer<ExperienceInterfaceBlockEntity, PowerGeneratorRenderState> {
    public static final float ROTATION_MAGIC = 0.001220703125F;
    public static final float ELEVATION = 0.5F;
    public static final StandaloneModelKey<BlockStateModel> MODEL = new StandaloneModelKey<>(
        () -> "AnvilCraft-PigsPlus: Experience Interface Converter Cube Model"
    );

    public ExperienceInterfaceRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public PowerGeneratorRenderState createRenderState() {
        return new PowerGeneratorRenderState();
    }

    @Override
    public void extractRenderState(
        ExperienceInterfaceBlockEntity be,
        PowerGeneratorRenderState state,
        float partialTicks,
        Vec3 cameraPosition,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);
        state.setElevation(ELEVATION);
        state.setRotation(this.rotation(be, partialTicks));
        state.setCube(FeatureRendererSupport.initialize(MODEL, be));
    }

    @Override
    public void submit(PowerGeneratorRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        pose.translate(0.5F, state.getElevation(), 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(state.getRotation()));
        pose.mulPose(Axis.ZP.rotationDegrees(state.getRotation()));
        state.getCube().submit(pose, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        pose.popPose();
    }

    protected float rotation(ExperienceInterfaceBlockEntity blockEntity, float partialTick) {
        return ((float) blockEntity.getTime() + partialTick) * 5 * ROTATION_MAGIC * 50.0F;
    }
}
