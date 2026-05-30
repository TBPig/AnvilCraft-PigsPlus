package dev.anvilcraft.pigsplus.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.anvilcraft.pigsplus.block.entity.ExperienceInterfaceBlockEntity;
import dev.anvilcraft.pigsplus.client.renderer.blockentity.state.ExperienceInterfaceRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ExperienceInterfaceRenderer implements BlockEntityRenderer<ExperienceInterfaceBlockEntity, ExperienceInterfaceRenderState> {

    public ExperienceInterfaceRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public ExperienceInterfaceRenderState createRenderState() {
        return new ExperienceInterfaceRenderState();
    }

    @Override
    public void extractRenderState(
        ExperienceInterfaceBlockEntity be,
        ExperienceInterfaceRenderState state,
        float partialTicks,
        Vec3 cameraPosition,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);
    }

    @Override
    public void submit(ExperienceInterfaceRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
    }
}
