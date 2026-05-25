package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.inventory.AutoRoyalSmithingMenu;
import dev.dubhe.anvilcraft.AnvilCraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;
import java.util.Optional;

public class AutoRoyalSmithingScreen extends AbstractContainerScreen<AutoRoyalSmithingMenu> {
    private static final Identifier BACKGROUND =
        AnvilCraft.of("textures/gui/container/smithing/background/royal_smithing_table.png");
    private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM =
        Identifier.fromNamespaceAndPath("minecraft", "item/empty_slot_smithing_template_armor_trim");
    private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE =
        Identifier.fromNamespaceAndPath("minecraft", "item/empty_slot_smithing_template_netherite_upgrade");
    private static final Component MISSING_TEMPLATE_TOOLTIP =
        Component.translatable("container.upgrade.missing_template_tooltip");
    private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
    private static final List<Identifier> EMPTY_SLOT_SMITHING_TEMPLATES =
        List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);

    private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(AutoRoyalSmithingMenu.TE_INVENTORY_FIRST_SLOT_INDEX);
    private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(AutoRoyalSmithingMenu.TE_INVENTORY_FIRST_SLOT_INDEX+ 1);
    private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(AutoRoyalSmithingMenu.TE_INVENTORY_FIRST_SLOT_INDEX+2);

    public AutoRoyalSmithingScreen(AutoRoyalSmithingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.getImageWidth() - this.font.width(this.title)) / 2;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        Optional<SmithingTemplateItem> optional = this.getTemplateItem();
        this.templateIcon.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.baseIcon.tick(
            optional.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
        this.additionalIcon.tick(
            optional.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
    }
    
    private Optional<SmithingTemplateItem> getTemplateItem() {
        ItemStack itemStack = this.menu.getSlot(AutoRoyalSmithingMenu.TE_INVENTORY_FIRST_SLOT_INDEX).getItem();
        Item item;
        if (!itemStack.isEmpty() && (item = itemStack.getItem()) instanceof SmithingTemplateItem) {
            SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem) item;
            return Optional.of(smithingTemplateItem);
        }
        return Optional.empty();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        this.extractOnboardingTooltips(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.getImageWidth(), this.getImageHeight(), this.getImageWidth(), this.getImageHeight());
        
        // 渲染槽位背景图标
        this.templateIcon.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.baseIcon.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.additionalIcon.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
    }


    private void extractOnboardingTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Optional<Component> optional = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(83, 48, 16, 16, mouseX, mouseY)) {
            optional = Optional.of(ERROR_TOOLTIP);
        }
        if (this.hoveredSlot != null) {
            ItemStack itemStack = this.menu.getSlot(0).getItem();
            ItemStack itemStack2 = this.hoveredSlot.getItem();
            if (itemStack.isEmpty()) {
                if (this.hoveredSlot.index == 0) {
                    optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
                }
            } else {
                Item item = itemStack.getItem();
                if (item instanceof SmithingTemplateItem smithingTemplateItem) {
                    if (itemStack2.isEmpty()) {
                        if (this.hoveredSlot.index == 1) {
                            optional = Optional.of(smithingTemplateItem.getBaseSlotDescription());
                        } else if (this.hoveredSlot.index == 2) {
                            optional = Optional.of(smithingTemplateItem.getAdditionSlotDescription());
                        }
                    }
                }
            }
        }
        optional.ifPresent(component -> graphics.setTooltipForNextFrame(
            this.font,
            this.font.split(component, 115),
            mouseX,
            mouseY
        ));
    }
    
    private boolean hasRecipeError() {
        // 检查是否有模板、基础物品和材料，但没有结果
        boolean hasTemplate = !this.menu.getSlot(0).getItem().isEmpty();
        boolean hasBase = !this.menu.getSlot(1).getItem().isEmpty();
        boolean hasAddition = !this.menu.getSlot(2).getItem().isEmpty();
        
        return hasTemplate && hasBase && hasAddition && this.menu.getResultSlot().getItem().isEmpty();
    }
}