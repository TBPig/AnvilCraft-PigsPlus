package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.block.entity.AutoRoyalGrindstoneBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.inventory.component.ReadOnlySlot;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

import static dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu.REPAIR_COST_RECIPES;

@Getter
public class AutoRoyalGrindstoneMenu extends AutoMachineMenu {
    public final AutoRoyalGrindstoneBlockEntity blockEntity;
    @Getter
    private final Slot resultToolSlot;
    @Getter
    private final Slot resultMaterialSlot;

    public AutoRoyalGrindstoneMenu(
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
    public AutoRoyalGrindstoneMenu(MenuType<?> menuType, int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(menuType, containerId, inventory, blockEntity);
        AutoRoyalGrindstoneMenu.checkContainerSize(inventory, 9);
        this.blockEntity = (AutoRoyalGrindstoneBlockEntity) blockEntity;

        this.addMachine();
        this.addSlot(resultToolSlot = new ReadOnlySlot(new SimpleContainer(1), 0, 152, 51));
        this.addSlot(resultMaterialSlot = new ReadOnlySlot(new SimpleContainer(1), 0, 35, 45));

        this.onChanged();
        this.addSlotListener(this);
    }

    @Override
    protected int get_te_inventory_slot_count() {
        return 2;
    }

    protected void addMachine() {
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 15, 33));
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 35, 21));
    }

    protected void onChanged() {
        this.resultToolSlot.set(blockEntity.getResultToolStack());
        this.resultMaterialSlot.set(blockEntity.getResultMaterialStack());
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
            ContainerLevelAccess.create(level, blockEntity.getPos()),
            player,
            AddonBlocks.AUTO_ROYAL_GRINDSTONE_BLOCK.get()
        );
    }

    // 移动物品到可用槽位
    protected boolean moveItemToActiveSlot(ItemStack stack) {
        int start_index = TE_INVENTORY_FIRST_SLOT_INDEX;
        int count = stack.getCount();
        if (!REPAIR_COST_RECIPES.containsKey(stack.getItem())) {
            moveItemStackTo(stack, start_index, start_index + 1, false);
        } else {
            moveItemStackTo(stack, start_index + 1, start_index + 2, false);
        }
        return stack.getCount() < count;
    }
}