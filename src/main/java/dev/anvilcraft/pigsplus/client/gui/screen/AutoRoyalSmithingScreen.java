package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.inventory.AutoRoyalSmithingMenu;
import dev.dubhe.anvilcraft.AnvilCraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;
import java.util.Optional;

public class AutoRoyalSmithingScreen extends AbstractContainerScreen<AutoRoyalSmithingMenu> {
    private static final ResourceLocation SMITHING_LOCATION =
        AnvilCraft.of("textures/gui/container/smithing/background/royal_smithing_table.png");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM =
        ResourceLocation.withDefaultNamespace("item/empty_slot_smithing_template_armor_trim");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE =
        ResourceLocation.withDefaultNamespace("item/empty_slot_smithing_template_netherite_upgrade");
    private static final Component MISSING_TEMPLATE_TOOLTIP =
        Component.translatable("container.upgrade.missing_template_tooltip");
    private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
    private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES =
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
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(SMITHING_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        
        // 渲染槽位背景图标
        this.templateIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        this.baseIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        this.additionalIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderOnboardingTooltips(guiGraphics, mouseX, mouseY);
    }

    
    private void renderOnboardingTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Optional<Component> optional = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(63, 38, 16, 16, mouseX, mouseY)) {
            optional = Optional.of(ERROR_TOOLTIP);
        }
        
        if (this.hoveredSlot != null) {
            ItemStack templateStack = this.menu.getSlot(0).getItem();
            ItemStack itemStack = this.hoveredSlot.getItem();
            
            if (templateStack.isEmpty()) {
                if (this.hoveredSlot.index == 0) { // 模板槽位
                    optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
                }
            } else {
                Item item = templateStack.getItem();
                if (item instanceof SmithingTemplateItem smithingTemplateItem) {
                    if (itemStack.isEmpty()) {
                        if (this.hoveredSlot.index == 1) { // 基础物品槽位
                            optional = Optional.of(smithingTemplateItem.getBaseSlotDescription());
                        } else if (this.hoveredSlot.index == 2) { // 材料槽位
                            optional = Optional.of(smithingTemplateItem.getAdditionSlotDescription());
                        }
                    }
                }
            }
        }
        
        optional.ifPresent(
            component -> guiGraphics.renderTooltip(this.font, this.font.split(component, 115), mouseX, mouseY));
    }
    
    private boolean hasRecipeError() {
        // 检查是否有模板、基础物品和材料，但没有结果
        boolean hasTemplate = !this.menu.getSlot(0).getItem().isEmpty();
        boolean hasBase = !this.menu.getSlot(1).getItem().isEmpty();
        boolean hasAddition = !this.menu.getSlot(2).getItem().isEmpty();
        
        return hasTemplate && hasBase && hasAddition && this.menu.getResultSlot().getItem().isEmpty();
    }
}