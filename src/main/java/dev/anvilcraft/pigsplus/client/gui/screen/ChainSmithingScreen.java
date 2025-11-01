package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.inventory.ChainSmithingMenu;
import dev.dubhe.anvilcraft.AnvilCraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;
import java.util.Optional;

public class ChainSmithingScreen extends ItemCombinerScreen<ChainSmithingMenu> {
    private static final ResourceLocation SMITHING_LOCATION =
        AnvilCraftPigsPlus.of("textures/gui/container/background/chain_smithing_table.png");
    private static final ResourceLocation ERROR =
        AnvilCraft.of("textures/gui/container/smithing/error.png");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM =
        ResourceLocation.withDefaultNamespace("item/empty_slot_smithing_template_armor_trim");
    private static final ResourceLocation EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE =
        ResourceLocation.withDefaultNamespace("item/empty_slot_smithing_template_netherite_upgrade");
    private static final Component MISSING_TEMPLATE_TOOLTIP =
        Component.translatable("container.upgrade.missing_template_tooltip");
    private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
    private static final List<ResourceLocation> EMPTY_SLOT_SMITHING_TEMPLATES =
        List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);
    private final CyclingSlotBackground templateIcon = new CyclingSlotBackground(0);
    private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(4);
    private final CyclingSlotBackground additionalIcon = new CyclingSlotBackground(5);

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
    }

    @Override
    protected void subInit() {
        // 不展示盔甲架，移除相关初始化代码
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
        Item item;
        // 检查前四个模板槽位
        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = this.menu.getSlot(i).getItem();
            if (!itemStack.isEmpty() && (item = itemStack.getItem()) instanceof SmithingTemplateItem) {
                SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem) item;
                return Optional.of(smithingTemplateItem);
            }
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
        this.templateIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        this.baseIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
        this.additionalIcon.render(this.menu, guiGraphics, partialTick, this.leftPos, this.topPos);
    }

    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
        // 不展示盔甲架，移除相关逻辑
    }

    @Override
    protected void renderErrorIcon(GuiGraphics guiGraphics, int x, int y) {
        if (this.hasRecipeError()) {
            guiGraphics.blit(ERROR, x + 83, y + 48, 0, 0, 16, 16, 16, 16);
        }
    }

    private void renderOnboardingTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Optional<Component> optional = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(83, 48, 16, 16, mouseX, mouseY)) {
            optional = Optional.of(ERROR_TOOLTIP);
        }
        if (this.hoveredSlot != null) {
            ItemStack itemStack = ItemStack.EMPTY;
            // 获取第一个非空模板槽位的物品
            for (int i = 0; i < 4; i++) {
                ItemStack stack = this.menu.getSlot(i).getItem();
                if (!stack.isEmpty()) {
                    itemStack = stack;
                    break;
                }
            }
            
            ItemStack itemStack2 = this.hoveredSlot.getItem();
            if (itemStack.isEmpty()) {
                if (this.hoveredSlot.index < 4) { // 模板槽位
                    optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
                }
            } else {
                Item item = itemStack.getItem();
                if (item instanceof SmithingTemplateItem smithingTemplateItem) {
                    if (itemStack2.isEmpty()) {
                        if (this.hoveredSlot.index == 4) { // 基础物品槽位
                            optional = Optional.of(smithingTemplateItem.getBaseSlotDescription());
                        } else if (this.hoveredSlot.index >= 5 && this.hoveredSlot.index <= 8) { // 材料槽位
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