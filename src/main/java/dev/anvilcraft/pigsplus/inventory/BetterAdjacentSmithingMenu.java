package dev.anvilcraft.pigsplus.inventory;

import dev.dubhe.anvilcraft.init.ModDataAttachments;
import dev.dubhe.anvilcraft.inventory.AdjacentSmithingMenu;
import dev.dubhe.anvilcraft.inventory.SmithingTemplateFavorites;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 支持多个模板槽从相邻容器临时借用锻造模板的菜单基类。
 *
 * <p>原版 AnvilCraft 的 {@link AdjacentSmithingMenu} 固定管理 0 号模板槽。本类继续继承它以复用
 * 模板目录扫描、收藏排序和网络同步，同时独立管理由子类声明的多个模板槽。</p>
 */
public abstract class BetterAdjacentSmithingMenu extends AdjacentSmithingMenu {
    private final Level templateLevel;
    private final Player menuPlayer;
    private final int[] templateSlots;
    private final List<BorrowedTemplate> borrowedTemplates;
    private final DataSlot borrowedTemplateMask;

    @Nullable
    private BlockPos tablePos;

    protected BetterAdjacentSmithingMenu(
        MenuType<?> type,
        int containerId,
        Inventory playerInventory,
        ContainerLevelAccess access,
        int... templateSlots
    ) {
        super(type, containerId, playerInventory, access);
        if (templateSlots.length == 0 || templateSlots.length > Integer.SIZE) {
            throw new IllegalArgumentException("Template slot count must be between 1 and " + Integer.SIZE);
        }
        this.templateLevel = playerInventory.player.level();
        this.menuPlayer = playerInventory.player;
        this.templateSlots = templateSlots.clone();
        this.validateTemplateSlots();
        this.borrowedTemplates = new ArrayList<>(templateSlots.length);
        for (int ignored : templateSlots) {
            this.borrowedTemplates.add(null);
        }
        this.borrowedTemplateMask = DataSlot.standalone();
        this.addDataSlot(this.borrowedTemplateMask);
        access.execute((level, pos) -> this.tablePos = pos.immutable());
    }

    @Override
    public List<ItemStack> getAdjacentTemplates() {
        List<ItemStack> templates = new ArrayList<>(super.getAdjacentTemplates());
        for (int templateSlot : this.templateSlots) {
            ItemStack stack = this.inputSlots.getItem(templateSlot);
            if (!stack.isEmpty() && this.isBorrowedTemplateSlot(templateSlot)) {
                addUniqueTemplate(templates, stack);
            }
        }
        return templates;
    }

    @Override
    public boolean isBorrowedTemplate(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (int templateSlot : this.templateSlots) {
            ItemStack borrowedStack = this.inputSlots.getItem(templateSlot);
            if (this.isBorrowedTemplateSlot(templateSlot)
                && !borrowedStack.isEmpty()
                && stack.is(borrowedStack.getItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleTemplateAction(Player player, ResourceLocation template, boolean toggleFavorite) {
        if (!(player instanceof ServerPlayer) || player != this.menuPlayer || this.tablePos == null) return;
        if (!this.containsVisibleTemplate(template)) return;

        if (toggleFavorite) {
            SmithingTemplateFavorites favorites = player.getData(ModDataAttachments.SMITHING_TEMPLATE_FAVORITES);
            player.setData(ModDataAttachments.SMITHING_TEMPLATE_FAVORITES, favorites.toggle(template));
            return;
        }

        BorrowedTemplate borrowedTemplate = this.findBorrowedTemplate(template);
        if (borrowedTemplate != null) {
            this.returnBorrowedTemplate(borrowedTemplate, true);
            return;
        }

        int templateIndex = this.firstAvailableTemplateIndex();
        if (templateIndex < 0) return;
        ExtractedTemplate extracted = this.extractTemplate(template);
        if (extracted == null) return;

        int inputSlot = this.templateSlots[templateIndex];
        this.borrowedTemplates.set(templateIndex, new BorrowedTemplate(
            inputSlot,
            extracted.sourcePos(),
            extracted.sourceSlot(),
            extracted.stack().copyWithCount(1),
            extracted.sourceBlockEntity()
        ));
        this.setBorrowedTemplateSlot(templateIndex, true);
        this.inputSlots.setItem(inputSlot, extracted.stack());
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (this.isBorrowedTemplateSlot(slotId)) return;
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (this.isBorrowedTemplateSlot(index)) return ItemStack.EMPTY;
        if (index > this.getResultSlot() && index < this.slots.size()) {
            ItemStack stack = this.getSlot(index).getItem();
            if (this.wouldMergeIntoBorrowedTemplate(stack)) return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, index);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (this.isBorrowedTemplateSlot(slot)) return false;
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return !this.isBorrowedTemplateSlot(slot) && super.canDragTo(slot);
    }

    @Override
    public void removed(Player player) {
        if (!this.templateLevel.isClientSide) {
            for (BorrowedTemplate borrowedTemplate : new ArrayList<>(this.borrowedTemplates)) {
                if (borrowedTemplate != null) {
                    this.returnBorrowedTemplate(borrowedTemplate, false);
                }
            }
        }
        super.removed(player);
    }

    protected final boolean isBorrowedTemplateSlot(int slot) {
        int templateIndex = this.getTemplateIndex(slot);
        return templateIndex >= 0 && this.isBorrowedTemplateIndex(templateIndex);
    }

    private boolean isBorrowedTemplateSlot(Slot slot) {
        for (int templateSlot : this.templateSlots) {
            if (slot == this.getSlot(templateSlot)) {
                return this.isBorrowedTemplateSlot(templateSlot);
            }
        }
        return false;
    }

    private void validateTemplateSlots() {
        for (int index = 0; index < this.templateSlots.length; index++) {
            int slot = this.templateSlots[index];
            if (slot < 0 || slot >= this.getResultSlot()) {
                throw new IllegalArgumentException("Invalid template slot: " + slot);
            }
            for (int previous = 0; previous < index; previous++) {
                if (this.templateSlots[previous] == slot) {
                    throw new IllegalArgumentException("Duplicate template slot: " + slot);
                }
            }
        }
    }

    private boolean containsVisibleTemplate(ResourceLocation template) {
        return this.getAdjacentTemplates().stream().anyMatch(stack -> matchesTemplate(stack, template));
    }

    private int firstAvailableTemplateIndex() {
        for (int index = 0; index < this.templateSlots.length; index++) {
            int slot = this.templateSlots[index];
            if (this.borrowedTemplates.get(index) == null && this.inputSlots.getItem(slot).isEmpty()) {
                return index;
            }
        }
        return -1;
    }

    @Nullable
    private BorrowedTemplate findBorrowedTemplate(ResourceLocation template) {
        for (BorrowedTemplate borrowedTemplate : this.borrowedTemplates) {
            if (borrowedTemplate != null && itemId(borrowedTemplate.stack()).equals(template)) {
                return borrowedTemplate;
            }
        }
        return null;
    }

    private boolean wouldMergeIntoBorrowedTemplate(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (int templateSlot : this.templateSlots) {
            if (!this.isBorrowedTemplateSlot(templateSlot)) continue;
            ItemStack borrowedStack = this.inputSlots.getItem(templateSlot);
            if (ItemStack.isSameItemSameComponents(stack, borrowedStack)) return true;
        }
        return false;
    }

    private int getTemplateIndex(int slot) {
        for (int index = 0; index < this.templateSlots.length; index++) {
            if (this.templateSlots[index] == slot) return index;
        }
        return -1;
    }

    private boolean isBorrowedTemplateIndex(int templateIndex) {
        return (this.borrowedTemplateMask.get() & 1 << templateIndex) != 0;
    }

    private void setBorrowedTemplateSlot(int templateIndex, boolean borrowed) {
        int mask = this.borrowedTemplateMask.get();
        this.borrowedTemplateMask.set(
            borrowed ? mask | 1 << templateIndex : mask & ~(1 << templateIndex)
        );
    }

    @Nullable
    private ExtractedTemplate extractTemplate(ResourceLocation template) {
        if (this.tablePos == null) return null;
        for (Direction direction : Direction.values()) {
            BlockPos sourcePos = this.tablePos.relative(direction);
            IItemHandler handler = this.getItemHandler(sourcePos);
            if (handler == null) continue;
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!matchesTemplate(stack, template) || !this.isUsableTemplate(stack)) continue;
                ItemStack simulated = handler.extractItem(slot, 1, true);
                if (!matchesTemplate(simulated, template) || !this.isUsableTemplate(simulated)) continue;
                ItemStack extracted = handler.extractItem(slot, 1, false);
                if (matchesTemplate(extracted, template) && this.isUsableTemplate(extracted)) {
                    return new ExtractedTemplate(
                        sourcePos.immutable(),
                        slot,
                        extracted,
                        this.templateLevel.getBlockEntity(sourcePos)
                    );
                }
                this.returnToHandlerOrDrop(handler, slot, extracted);
            }
        }
        return null;
    }

    private void returnBorrowedTemplate(BorrowedTemplate borrowedTemplate, boolean notifyMenu) {
        int inputSlot = borrowedTemplate.inputSlot();
        int templateIndex = this.getTemplateIndex(inputSlot);
        if (templateIndex < 0 || this.borrowedTemplates.get(templateIndex) != borrowedTemplate) return;

        ItemStack stack = this.inputSlots.getItem(inputSlot);
        this.borrowedTemplates.set(templateIndex, null);
        this.setBorrowedTemplateSlot(templateIndex, false);
        if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, borrowedTemplate.stack())) return;

        ItemStack returnedStack = stack.copyWithCount(1);
        if (stack.getCount() == 1) {
            if (notifyMenu) {
                this.inputSlots.setItem(inputSlot, ItemStack.EMPTY);
            } else {
                this.inputSlots.removeItemNoUpdate(inputSlot);
            }
        } else {
            stack.shrink(1);
            if (notifyMenu) {
                this.inputSlots.setItem(inputSlot, stack);
            }
        }

        IItemHandler handler =
            this.templateLevel.getBlockEntity(borrowedTemplate.sourcePos()) == borrowedTemplate.sourceBlockEntity()
                ? this.getItemHandler(borrowedTemplate.sourcePos())
                : null;
        this.returnToHandlerOrDrop(handler, borrowedTemplate.sourceSlot(), returnedStack);
    }

    @Nullable
    private IItemHandler getItemHandler(BlockPos pos) {
        IItemHandler handler = this.templateLevel.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (handler != null) return handler;
        if (this.templateLevel.getBlockEntity(pos) instanceof Container container) {
            return new InvWrapper(container);
        }
        return null;
    }

    private void returnToHandlerOrDrop(@Nullable IItemHandler handler, int preferredSlot, ItemStack stack) {
        ItemStack remainder = stack;
        if (handler != null && preferredSlot >= 0 && preferredSlot < handler.getSlots()) {
            remainder = handler.insertItem(preferredSlot, remainder, false);
        }
        if (handler != null && !remainder.isEmpty()) {
            for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
                if (slot == preferredSlot) continue;
                remainder = handler.insertItem(slot, remainder, false);
            }
        }
        if (remainder.isEmpty()) return;
        BlockPos dropPos = this.tablePos == null ? this.menuPlayer.blockPosition() : this.tablePos;
        Containers.dropItemStack(
            this.templateLevel,
            dropPos.getX() + 0.5,
            dropPos.getY() + 1.0,
            dropPos.getZ() + 0.5,
            remainder
        );
    }

    private static boolean matchesTemplate(ItemStack stack, ResourceLocation template) {
        return !stack.isEmpty() && itemId(stack).equals(template);
    }

    private static ResourceLocation itemId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    private static void addUniqueTemplate(List<ItemStack> templates, ItemStack stack) {
        if (templates.stream().anyMatch(existing -> existing.is(stack.getItem()))) return;
        templates.add(stack.copyWithCount(1));
    }

    private record BorrowedTemplate(
        int inputSlot,
        BlockPos sourcePos,
        int sourceSlot,
        ItemStack stack,
        @Nullable BlockEntity sourceBlockEntity
    ) {
    }

    private record ExtractedTemplate(
        BlockPos sourcePos,
        int sourceSlot,
        ItemStack stack,
        @Nullable BlockEntity sourceBlockEntity
    ) {
    }
}
