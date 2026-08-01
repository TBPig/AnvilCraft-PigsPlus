package dev.anvilcraft.pigsplus.client.jade;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.ui.Element;

/**
 * Jade 中直接绘制普通纹理的图标元素。
 */
public class TextureElement extends Element {
    private final ResourceLocation texture;
    private final int width;
    private final int height;

    public TextureElement(ResourceLocation texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    @Override
    public Vec2 getSize() {
        return new Vec2(this.width, this.height);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float alpha, float partialTick) {
        guiGraphics.blit(
            this.texture,
            (int) x,
            (int) y,
            0,
            0,
            this.width,
            this.height,
            this.width,
            this.height
        );
    }
}
