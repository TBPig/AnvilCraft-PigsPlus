package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.inventory.AutoRoyalGrindstoneMenu;
import dev.dubhe.anvilcraft.AnvilCraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class AutoRoyalGrindstoneScreen extends AbstractContainerScreen<AutoRoyalGrindstoneMenu> {
    private static final Identifier BACKGROUND =
        AnvilCraft.of("textures/gui/container/smithing/background/royal_grindstone.png");
    private final CyclingSlotBackground inputIcon1 = new CyclingSlotBackground(AutoRoyalGrindstoneMenu.TE_INVENTORY_FIRST_SLOT_INDEX);
    private final CyclingSlotBackground inputIcon2 = new CyclingSlotBackground(AutoRoyalGrindstoneMenu.TE_INVENTORY_FIRST_SLOT_INDEX + 1);

    public AutoRoyalGrindstoneScreen(AutoRoyalGrindstoneMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.getImageWidth() - this.font.width(this.title)) / 2;
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

        // 渲染槽位背景图标
        this.inputIcon1.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.inputIcon2.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
    }
}