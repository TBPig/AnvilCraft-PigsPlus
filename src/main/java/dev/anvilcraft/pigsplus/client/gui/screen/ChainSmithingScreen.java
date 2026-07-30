package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.inventory.ChainSmithingMenu;
import dev.dubhe.anvilcraft.client.gui.screen.AdjacentSmithingScreen;
import dev.dubhe.anvilcraft.constant.Constant;
import dev.dubhe.anvilcraft.constant.SharedTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;
import java.util.Optional;

public class ChainSmithingScreen extends AdjacentSmithingScreen<ChainSmithingMenu> {
    private static final ResourceLocation SMITHING_LOCATION =
        AnvilCraftPigsPlus.of("textures/gui/container/background/chain_smithing_table.png");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM =
        ResourceLocation.withDefaultNamespace("item/empty_slot_smithing_template_armor_trim");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE =
        ResourceLocation.withDefaultNamespace("item/empty_slot_smithing_template_netherite_upgrade");
    private static final Component MISSING_TEMPLATE_TOOLTIP =
        Component.translatable("container.upgrade.missing_template_tooltip");
    private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
    private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES =
        List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);
    private final CyclingSlotBackground[] templateIcons = {
        new CyclingSlotBackground(0),
        new CyclingSlotBackground(1),
        new CyclingSlotBackground(2),
        new CyclingSlotBackground(3)
    };
    private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(4);
    private final CyclingSlotBackground[] additionalIcons = {
        new CyclingSlotBackground(5),
        new CyclingSlotBackground(6),
        new CyclingSlotBackground(7),
        new CyclingSlotBackground(8)
    };

    public ChainSmithingScreen(
        ChainSmithingMenu menu,
        Inventory playerInventory,
        Component title
    ) {
        super(menu, playerInventory, title, SMITHING_LOCATION);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = Constant.SCREEN_TITLE_Y;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        Optional<SmithingTemplateItem> optional = this.getTemplateItem();
        for (CyclingSlotBackground slot : this.templateIcons) {
            slot.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        }
        this.baseIcon.tick(
            optional.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
        for (int i = 0; i < ChainSmithingMenu.MAX; i++) {
            Optional<SmithingTemplateItem> additionTemplate = this.getTemplateItem(i).or(this::getTemplateItem);
            this.additionalIcons[i].tick(
                additionTemplate.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
        }
    }

    private Optional<SmithingTemplateItem> getTemplateItem() {
        // 检查前四个模板槽位
        for (int i = 0; i < 4; i++) {
            Optional<SmithingTemplateItem> smithingTemplateItem = getTemplateItem(i);
            if (smithingTemplateItem.isPresent()) return smithingTemplateItem;
        }
        for (ItemStack stack : this.menu.getAdjacentTemplates()) {
            Optional<SmithingTemplateItem> smithingTemplateItem = getTemplateItem(stack);
            if (smithingTemplateItem.isPresent()) return smithingTemplateItem;
        }
        return Optional.empty();
    }

    private Optional<SmithingTemplateItem> getTemplateItem(int i) {
        return getTemplateItem(this.menu.getSlot(i).getItem());
    }

    private Optional<SmithingTemplateItem> getTemplateItem(ItemStack itemStack) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof SmithingTemplateItem smithingTemplateItem) {
            return Optional.of(smithingTemplateItem);
        }
        return Optional.empty();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderOnboardingTooltips(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        for (CyclingSlotBackground slot : this.templateIcons) {
            slot.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        }
        this.baseIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        for (CyclingSlotBackground slot : this.additionalIcons) {
            slot.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics guiGraphics, int x, int y) {
        if (this.hasRecipeError()) {
            guiGraphics.blit(SharedTextures.ERROR_SPRITE, x + 131, y + 38, 0, 0, 16, 16, 16, 16);
        }
    }

    private void renderOnboardingTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Optional<Component> optional = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(131, 38, 16, 16, mouseX, mouseY)) {
            optional = Optional.of(ERROR_TOOLTIP);
        }
        if (this.hoveredSlot != null) {
            if (this.hoveredSlot.index < 4 && this.hoveredSlot.getItem().isEmpty()) { // 空模板槽位
                optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
            } else if (this.hoveredSlot.index == 4) {
                Optional<SmithingTemplateItem> templateItem = this.getTemplateItem();
                if (templateItem.isPresent()) {
                    SmithingTemplateItem smithingTemplateItem = templateItem.get();
                    optional = Optional.of(smithingTemplateItem.getBaseSlotDescription());
                }
            } else if (this.hoveredSlot.index >= 5 && this.hoveredSlot.index <= 8) {
                Optional<SmithingTemplateItem> templateItem =
                    this.getTemplateItem(this.hoveredSlot.index - ChainSmithingMenu.MAX - 1).or(this::getTemplateItem);
                if (templateItem.isPresent()) {
                    SmithingTemplateItem smithingTemplateItem = templateItem.get();
                    optional = Optional.of(smithingTemplateItem.getAdditionSlotDescription());
                }
            }
        }

        optional.ifPresent(
            component -> guiGraphics.renderTooltip(this.font, this.font.split(component, 115), mouseX, mouseY));
    }

    private boolean hasRecipeError() {
        // 检查是否有模板、基础物品和材料，但没有结果
        boolean hasTemplate = false;
        boolean hasBase = !this.menu.getSlot(4).getItem().isEmpty();
        boolean hasAddition = false;

        // 检查模板槽位
        for (int i = 0; i < 4; i++) {
            if (!this.menu.getSlot(i).getItem().isEmpty()) {
                hasTemplate = true;
                break;
            }
        }
        if (!hasTemplate && !this.menu.getAdjacentTemplates().isEmpty()) {
            hasTemplate = true;
        }

        // 检查材料槽位
        for (int i = 5; i < 9; i++) {
            if (!this.menu.getSlot(i).getItem().isEmpty()) {
                hasAddition = true;
                break;
            }
        }

        return hasTemplate && hasBase && hasAddition && this.menu.getSlot(9).getItem().isEmpty();
    }
}
