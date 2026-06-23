package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.inventory.ChainSmithingMenu;
import dev.dubhe.anvilcraft.constant.Constant;
import dev.dubhe.anvilcraft.constant.SharedTextures;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;
import java.util.Optional;

public class ChainSmithingScreen extends ItemCombinerScreen<ChainSmithingMenu> {
    private static final Identifier SMITHING_LOCATION =
        AnvilCraftPigsPlus.of("textures/gui/container/background/chain_smithing_table.png");
    private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM =
        Identifier.withDefaultNamespace("container/slot/smithing_template_armor_trim");
    private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE =
        Identifier.withDefaultNamespace("container/slot/smithing_template_netherite_upgrade");
    private static final Component MISSING_TEMPLATE_TOOLTIP =
        Component.translatable("container.upgrade.missing_template_tooltip");
    private static final Component ERROR_TOOLTIP = Component.translatable("container.upgrade.error_tooltip");
    private static final List<Identifier> EMPTY_SLOT_SMITHING_TEMPLATES =
        List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE);
    private final CyclingSlotBackground templateIcon0 = new CyclingSlotBackground(0);
    private final CyclingSlotBackground templateIcon1 = new CyclingSlotBackground(1);
    private final CyclingSlotBackground templateIcon2 = new CyclingSlotBackground(2);
    private final CyclingSlotBackground templateIcon3 = new CyclingSlotBackground(3);
    private final CyclingSlotBackground baseIcon = new CyclingSlotBackground(4);
    private final List<CyclingSlotBackground> addtionalIcons = List.of(
        new CyclingSlotBackground(5),
        new CyclingSlotBackground(6),
        new CyclingSlotBackground(7),
        new CyclingSlotBackground(8)
    );

    public ChainSmithingScreen(
        ChainSmithingMenu menu,
        Inventory playerInventory,
        Component title
    ) {
        super(menu, playerInventory, title, SMITHING_LOCATION);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.getImageWidth() - this.font.width(this.title)) / 2;
        this.titleLabelY = Constant.SCREEN_TITLE_Y;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        Optional<SmithingTemplateItem> optional = this.getTemplateItem(0);
        this.templateIcon0.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.templateIcon1.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.templateIcon2.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.templateIcon3.tick(EMPTY_SLOT_SMITHING_TEMPLATES);
        this.baseIcon.tick(
            optional.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
        for (int i = 0; i < addtionalIcons.size(); i++) {
            this.addtionalIcons.get(i).tick(getTemplateItem(i).map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of()));
        }
    }

    private Optional<SmithingTemplateItem> getTemplateItem(int i) {
        Item item;
        // 检查前四个模板槽位
        ItemStack itemStack = this.menu.getSlot(i).getItem();
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
        this.templateIcon0.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.templateIcon1.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.templateIcon2.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.templateIcon3.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.baseIcon.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos);
        this.addtionalIcons.forEach(icon -> icon.extractRenderState(this.menu, graphics, a, this.leftPos, this.topPos));
    }

    @Override
    protected void extractErrorIcon(GuiGraphicsExtractor graphics, int x, int y) {
        if (this.hasRecipeError()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SharedTextures.ERROR_SPRITE, x + 131, y + 38, 0, 0, 16, 16, 16, 16);
        }
    }

    private void extractOnboardingTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Optional<Component> optional = Optional.empty();
        if (this.hasRecipeError() && this.isHovering(131, 38, 16, 16, mouseX, mouseY)) {
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
        optional.ifPresent(component -> graphics.setTooltipForNextFrame(
            this.font,
            this.font.split(component, 115),
            mouseX,
            mouseY
        ));
    }

    private boolean hasRecipeError() {
        return ChainSmithingMenu.hasRecipeError(this.menu);
    }
}