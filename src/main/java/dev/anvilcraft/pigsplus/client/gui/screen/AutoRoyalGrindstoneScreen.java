package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.inventory.AutoRoyalGrindstoneMenu;
import dev.dubhe.anvilcraft.constant.Constant;
import dev.dubhe.anvilcraft.constant.SharedTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AutoRoyalGrindstoneScreen extends AbstractContainerScreen<AutoRoyalGrindstoneMenu> {
    private static final ResourceLocation BACKGROUND = SharedTextures.bg("crafting", "royal_grindstone");
    private final CyclingSlotBackground inputIcon1 = new CyclingSlotBackground(AutoRoyalGrindstoneMenu.TE_INVENTORY_FIRST_SLOT_INDEX);
    private final CyclingSlotBackground inputIcon2 = new CyclingSlotBackground(AutoRoyalGrindstoneMenu.TE_INVENTORY_FIRST_SLOT_INDEX + 1);

    public AutoRoyalGrindstoneScreen(AutoRoyalGrindstoneMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = Constant.SCREEN_TITLE_Y;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        g.blit(BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);

        // 渲染槽位背景图标
        this.inputIcon1.render(this.menu, g, partialTick, this.leftPos, this.topPos);
        this.inputIcon2.render(this.menu, g, partialTick, this.leftPos, this.topPos);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}