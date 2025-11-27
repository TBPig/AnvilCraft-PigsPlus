package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.block.entity.AutoRoyalSmithingTableBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.inventory.BaseMachineMenu;
import dev.dubhe.anvilcraft.inventory.component.ReadOnlySlot;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.List;

@Getter
public class AutoRoyalSmithingMenu extends BaseMachineMenu implements ContainerListener {
    public final AutoRoyalSmithingTableBlockEntity blockEntity;
    @Getter
    private final Slot resultSlot;
    private final Level level;
    private final List<RecipeHolder<SmithingRecipe>> recipes;

    public AutoRoyalSmithingMenu(
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
    public AutoRoyalSmithingMenu(MenuType<?> menuType, int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(menuType, containerId, blockEntity);
        AutoRoyalSmithingMenu.checkContainerSize(inventory, 9);

        this.blockEntity = (AutoRoyalSmithingTableBlockEntity) blockEntity;
        this.level = inventory.player.level();
        this.recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);

        this.addPlayerHotbar(inventory);
        this.addPlayerInventory(inventory);
        this.addMachine();
        this.addSlot(resultSlot = new ReadOnlySlot(new SimpleContainer(1), 0, 106, 48));

        this.onChanged();
        this.addSlotListener(this);
    }

    private void addMachine() {
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 8, 48));
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 44, 48));
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 62, 48));
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    private void onChanged() {
        ItemStack resultItem = blockEntity.getResult();
        this.resultSlot.set(resultItem);
    }


    // 功劳归于：: diesieben07 | https://github.com/diesieben07/SevenCommons
    // 必须为 GUI 使用的每个插槽分配一个插槽编号.
    // 对于这个容器，我们可以看到瓷砖库存的插槽以及玩家库存插槽和快捷栏.
    // 每次我们向容器添加 Slot 时，它都会自动增加 slotIndex，这意味着
    //  0 - 8 = 快捷栏插槽（将映射到 InventoryPlayer 插槽编号 0 - 8）
    //  9 - 35 = 玩家物品栏（映射到 InventoryPlayer 插槽编号 9 - 35）
    //  36 - 38 = TileInventory 插槽，映射到我们的 TileEntity 插槽编号 0 - 8）
    public static final int HOTBAR_SLOT_COUNT = 9;
    public static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    public static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    public static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    public static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    public static final int VANILLA_FIRST_SLOT_INDEX = 0;
    public static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 3; // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        //noinspection ConstantValue
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY; // EMPTY_ITEM
        }
        ItemStack sourceStack = sourceSlot.getItem();
        final ItemStack copyOfSourceStack = sourceStack.copy();
        // Check if the slot clicked is one of the vanilla container slots
        if (index < TE_INVENTORY_FIRST_SLOT_INDEX) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemToActiveSlot(sourceStack)) {
                return ItemStack.EMPTY; // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
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
            AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK.get()
        );
    }

    // 移动物品到可用槽位
    private boolean moveItemToActiveSlot(ItemStack stack) {
        int start_index = TE_INVENTORY_FIRST_SLOT_INDEX;
        int count = stack.getCount();
        // 检查是否为模板材料
        if (this.recipes.stream().anyMatch(smithingRecipe -> smithingRecipe.value().isTemplateIngredient(stack))) {
            moveItemStackTo(stack, start_index, start_index + 1, false);

        }
        // 检查是否为基础物品
        else if (this.recipes.stream().anyMatch(smithingRecipe -> smithingRecipe.value().isBaseIngredient(stack))) {
            moveItemStackTo(stack, start_index + 1, start_index + 2, false);
        }
        // 检查是否为添加材料
        else if (this.recipes.stream().anyMatch(smithingRecipe -> smithingRecipe.value().isAdditionIngredient(stack))) {
            moveItemStackTo(stack, start_index + 2, start_index + 3, false);
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