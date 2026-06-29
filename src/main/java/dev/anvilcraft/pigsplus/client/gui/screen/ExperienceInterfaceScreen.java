package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.inventory.ExperienceInterfaceMenu;
import dev.anvilcraft.pigsplus.network.ExperienceInterfaceUpdatePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ExperienceInterfaceScreen extends AbstractContainerScreen<ExperienceInterfaceMenu> {
    public static final ResourceLocation BACKGROUND =
        AnvilCraftPigsPlus.of("textures/gui/container/background/experience_interface.png");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 77;
    private @Nullable EditBox valueInput = null;
    private int lastValidValue;

    public ExperienceInterfaceScreen(ExperienceInterfaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 3;

        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;

        this.valueInput = new EditBox(this.font, offsetX + 30, offsetY + 34, 116, 16, Component.literal("value"));
        this.valueInput.setMaxLength(12);
        this.valueInput.setValue(String.valueOf(lastValidValue));
        this.valueInput.setResponder(this::onValueChanged);
        this.addRenderableWidget(this.valueInput);
        this.setInitialFocus(this.valueInput);
    }

    private void onValueChanged(String value) {
        if (value.isEmpty()) return;
        if (!value.matches("^[0-9]+$")) {
            if (this.valueInput != null) {
                this.valueInput.setValue(String.valueOf(lastValidValue));
            }
            return;
        }
        try {
            int parsed = Integer.parseUnsignedInt(value);
            this.lastValidValue = parsed;
            PacketDistributor.sendToServer(new ExperienceInterfaceUpdatePacket(parsed));
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    public void setValue(int value) {
        this.lastValidValue = value;
        if (this.valueInput != null) {
            this.valueInput.setValue(String.valueOf(value));
        }
    }
}
