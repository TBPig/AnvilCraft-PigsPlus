package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.block.entity.AutoJewelCraftingTableBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.inventory.component.ReadOnlySlot;
import dev.dubhe.anvilcraft.recipe.JewelCraftingRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.cache.RecipeCaches;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

@Getter
public class AutoJewelCraftingMenu extends AutoMachineMenu {
    public final AutoJewelCraftingTableBlockEntity blockEntity;
    @Getter
    private final Slot resultSlot;

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
        super(menuType, containerId, inventory, blockEntity);
        AutoJewelCraftingMenu.checkContainerSize(inventory, 9);
        this.blockEntity = (AutoJewelCraftingTableBlockEntity) blockEntity;

        this.addMachine();
        this.addSlot(resultSlot = new ReadOnlySlot(new SimpleContainer(1), 0, 134, 51));

        this.onChanged();
        this.addSlotListener(this);
    }

    @Override
    protected int get_te_inventory_slot_count() {
        return 5;
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

    protected void addMachine() {
        this.addSlot(new JewelCraftingSlotItemHandler(this.blockEntity.getItemHandler(), 0, 80, 19));
        for (int i = 0; i < 4; i++) {
            addSlot(new JewelCraftingSlotItemHandler(this.blockEntity.getItemHandler(), i + 1, 26 + i * 18, 51));
        }
    }


    protected void onChanged() {
        blockEntity.calcResult();
        this.resultSlot.set(blockEntity.getResultStack());
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
    protected boolean moveItemToActiveSlot(ItemStack stack) {
        int start_index = TE_INVENTORY_FIRST_SLOT_INDEX;
        int count = stack.getCount();
        if (RecipeCaches.getAllJewelResultItem().contains(stack.getItem())) {
            moveItemStackTo(stack, start_index, start_index + get_te_inventory_slot_count(), false);
        } else {
            moveItemStackTo(stack, start_index + 1, start_index + get_te_inventory_slot_count(), false);
        }
        return stack.getCount() < count;
    }
}