package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.block.entity.AutoJewelCraftingTableBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.inventory.component.JewelInputSlot;
import dev.anvilcraft.pigsplus.inventory.component.JewelResourceSlot;
import dev.dubhe.anvilcraft.inventory.component.ReadOnlySlot;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

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

    protected void addMachine() {
        ItemStacksResourceHandler handler = this.blockEntity.getItemHandler();
        this.addSlot(new JewelResourceSlot(handler, handler::set, 0, 80, 19));

        for (int i = 0; i < 4; i++) {
            addSlot(new JewelInputSlot(handler, handler::set, i + 1, 26 + i * 18, 51));
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

    @Override
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack) {
        if (blockEntity.getResultRecipe() == null) return;
        for (int j = 0; j < 4; j++) {
            Slot slot = abstractContainerMenu.getSlot(TE_INVENTORY_FIRST_SLOT_INDEX + 1 + i);
            if (slot instanceof JewelInputSlot slot1) {
                slot1.updateIngredient(blockEntity.getResultRecipe());
            }
        }
    }

    // 移动物品到可用槽位
    protected boolean moveItemToActiveSlot(ItemStack stack) {
        int start_index = TE_INVENTORY_FIRST_SLOT_INDEX;
        int count = stack.getCount();
        moveItemStackTo(stack, start_index, start_index + get_te_inventory_slot_count(), false);
        return stack.getCount() < count;
    }
}