package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.block.entity.AutoJewelCraftingTableBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.inventory.BaseMachineMenu;
import dev.dubhe.anvilcraft.inventory.component.ReadOnlySlot;
import dev.dubhe.anvilcraft.recipe.JewelCraftingRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.cache.RecipeCaches;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@Getter
public class AutoJewelCraftingMenu extends BaseMachineMenu implements ContainerListener {
    public final AutoJewelCraftingTableBlockEntity blockEntity;
    @Getter
    private final Slot resultSlot;
    private final Level level;

    public AutoJewelCraftingMenu(
        MenuType<?> menuType, int containerId, Inventory inventory, FriendlyByteBuf extraData
    ) {
        this(menuType, containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 合成器菜单
     *
     * @param menuType    菜单类型
     * @param containerId 容器id
     * @param inventory   背包
     * @param blockEntity 方块实体
     */
    public AutoJewelCraftingMenu(MenuType<?> menuType, int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(menuType, containerId, blockEntity);
        AutoJewelCraftingMenu.checkContainerSize(inventory, 9);

        this.blockEntity = (AutoJewelCraftingTableBlockEntity) blockEntity;
        this.level = inventory.player.level();

        this.addPlayerHotbar(inventory);
        this.addPlayerInventory(inventory);
        this.addMachine();
        this.addSlot(resultSlot = new ReadOnlySlot(new SimpleContainer(1), 0, 134, 51));

        this.onChanged();
        this.addSlotListener(this);
    }

    public class JewelCraftingSlotItemHandler extends SlotItemHandler {
        public JewelCraftingSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // slot 0 是源物品槽位
            int slot = this.index;
            if (slot == 0) {
                if (RecipeCaches.getAllJewelResultItem().contains(stack.getItem())) {
                    return super.mayPlace(stack);
                }
            }
            JewelCraftingRecipe resultRecipe = blockEntity.getResultRecipe();
            if (resultRecipe != null) {
                int idx = slot - 1;
                var mergedIngredients = resultRecipe.mergedIngredients;
                if (idx < mergedIngredients.size()) {
                    var entry = mergedIngredients.get(idx);
                    Ingredient ingredient = entry.getKey();
                    if (ingredient.test(stack)) {
                        return super.mayPlace(stack);
                    }
                }
            }
            return false;
        }
    }

    private void addMachine() {
        this.addSlot(new JewelCraftingSlotItemHandler(this.blockEntity.getItemHandler(), 0, 80, 19));
        for (int i = 0; i < 4; i++) {
            addSlot(new JewelCraftingSlotItemHandler(this.blockEntity.getItemHandler(), i + 1, 26 + i * 18, 51));
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    private void onChanged() {
        blockEntity.calcResult();
        this.resultSlot.set(blockEntity.getResultStack());
    }

    public static final int HOTBAR_SLOT_COUNT = 9;
    public static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    public static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    public static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    public static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    public static final int VANILLA_FIRST_SLOT_INDEX = 0;
    public static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 5; // must be the number of slots you have!


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY; // EMPTY_ITEM
        }
        ItemStack sourceStack = sourceSlot.getItem();
        final ItemStack copyOfSourceStack = sourceStack.copy();

        //noinspection ConstantValue
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }
        if (index < TE_INVENTORY_FIRST_SLOT_INDEX) {
            if (!moveItemToActiveSlot(sourceStack)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(
                sourceStack,
                VANILLA_FIRST_SLOT_INDEX,
                TE_INVENTORY_FIRST_SLOT_INDEX,
                false
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
            ContainerLevelAccess.create(level, blockEntity.getPos()),
            player,
            AddonBlocks.AUTO_JEWEL_CRAFTING_TABLE_BLOCK.get()
        );
    }

    // 移动物品到可用槽位
    private boolean moveItemToActiveSlot(ItemStack stack) {
        int start_index = TE_INVENTORY_FIRST_SLOT_INDEX;
        int count = stack.getCount();
        if (RecipeCaches.getAllJewelResultItem().contains(stack.getItem())) {
            moveItemStackTo(stack, start_index, start_index + TE_INVENTORY_SLOT_COUNT, false);
        } else {
            moveItemStackTo(stack, start_index + 1, start_index + TE_INVENTORY_SLOT_COUNT, false);
        }
        return stack.getCount() < count;
    }

    @Override
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack) {
        onChanged();
    }

    @Override
    public void dataChanged(AbstractContainerMenu abstractContainerMenu, int i, int i1) {
    }
}