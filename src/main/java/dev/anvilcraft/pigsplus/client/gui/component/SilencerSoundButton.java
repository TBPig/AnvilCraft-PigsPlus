package dev.anvilcraft.pigsplus.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.anvilcraft.pigsplus.client.gui.screen.PortableSilencerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SilencerSoundButton extends Button {
    private final ResourceLocation texture;
    private final int index;
    private final int variant;
    private final PortableSilencerScreen parent;

    public SilencerSoundButton(
        int x,
        int y,
        int index,
        int variant,
        OnPress onPress,
        PortableSilencerScreen parent,
        ResourceLocation texture
    ) {
        super(x, y, 112, 15, Component.empty(), onPress, var -> parent.getSoundTextAt(index, variant).copy());
        this.index = index;
        this.variant = variant;
        this.parent = parent;
        this.texture = texture;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation soundId = this.parent.getSoundIdAt(this.index, this.variant);
        if (soundId == null) return;
        this.renderTexture(guiGraphics, this.texture, this.getX(), this.getY(), 0, 0, 15);
        String searchText = this.parent.getFilterText();
        Component message;
        if (searchText.startsWith("#") || searchText.startsWith("~")) {
            message = this.parent.getSoundTextAt(this.index, this.variant);
        } else {
            message = highlighted(
                this.parent.getSoundTextAt(this.index, this.variant).getString(),
                searchText,
                ChatFormatting.WHITE,
                ChatFormatting.YELLOW
            );
        }
        this.setMessage(message);
        this.renderString(guiGraphics, Minecraft.getInstance().font, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);
        if (this.isHovered()) {
            Component soundIdText = this.tooltipLine(
                soundId,
                soundId.toString(),
                searchText.replaceFirst("#", ""),
                ChatFormatting.GRAY
            );
            guiGraphics.renderTooltip(
                Minecraft.getInstance().font,
                List.of(message.getVisualOrderText(), soundIdText.getVisualOrderText()),
                mouseX,
                mouseY
            );
        }
    }

    /**
     * 鼠标悬停时在声音 id 后补上本次统计到的播放次数，方便确认排序依据。
     */
    private Component tooltipLine(
        ResourceLocation soundId,
        String original,
        String highlightedText,
        ChatFormatting baseFormatting
    ) {
        int count = this.parent.getPlayCount(soundId);
        if (count <= 0) return highlighted(original, highlightedText, baseFormatting, ChatFormatting.YELLOW);
        return Component.empty()
            .append(highlighted(original, highlightedText, baseFormatting, ChatFormatting.YELLOW))
            .append(Component.literal(" x" + count).withStyle(ChatFormatting.DARK_GRAY));
    }

    private static Component highlighted(
        String original,
        String highlighted,
        ChatFormatting originalFormatting,
        ChatFormatting highlightFormatting
    ) {
        if (highlighted.isEmpty()) {
            return Component.literal(original).withStyle(originalFormatting);
        }
        try {
            String[] parts = original.split(Pattern.quote(highlighted), -1);
            List<Component> components = new ArrayList<>();
            for (String s : parts) {
                components.add(Component.literal(s).copy().setStyle(Style.EMPTY.applyFormat(originalFormatting)));
            }
            return ComponentUtils.formatList(
                components,
                Component.literal(highlighted).withStyle(highlightFormatting)
            );
        } catch (Throwable e) {
            return Component.literal(original);
        }
    }

    private void renderTexture(
        GuiGraphics guiGraphics,
        ResourceLocation texture,
        int x,
        int y,
        int uOffset,
        int vOffset,
        int hoverOffset
    ) {
        int v = vOffset + (this.isHovered() ? hoverOffset : 0);
        RenderSystem.enableDepthTest();
        guiGraphics.blit(texture, x, y, uOffset, v, this.width, this.height, 112, 30);
    }

    public int getIndex() {
        return this.index;
    }
}
