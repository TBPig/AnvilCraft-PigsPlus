package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.inventory.AutoJewelCraftingMenu;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.constant.Constant;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AutoJewelCraftingScreen extends AbstractContainerScreen<AutoJewelCraftingMenu> {
    private static final Identifier BACKGROUND =
        AnvilCraft.of("textures/gui/container/jewelcrafting/background.png");

    public AutoJewelCraftingScreen(AutoJewelCraftingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.getImageWidth() - this.font.width(this.title)) / 2;
        this.titleLabelY = Constant.SCREEN_TITLE_Y;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND,
            this.leftPos,
            this.topPos,
            0,
            0,
            this.getImageWidth(),
            this.getImageHeight(),
            this.getImageWidth(),
            this.getImageHeight()
        );
    }
}