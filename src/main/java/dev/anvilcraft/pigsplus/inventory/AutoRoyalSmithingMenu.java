package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.block.entity.AutoRoyalSmithingTableBlockEntity;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.List;

@Getter
public class AutoRoyalSmithingMenu extends AutoMachineMenu {
    public final AutoRoyalSmithingTableBlockEntity blockEntity;
    @Getter
    private final Slot resultSlot;
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
        super(menuType, containerId, inventory, blockEntity);
        AutoRoyalSmithingMenu.checkContainerSize(inventory, 9);
        this.blockEntity = (AutoRoyalSmithingTableBlockEntity) blockEntity;
        this.recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);

        this.addMachine();
        this.addSlot(resultSlot = new ReadOnlySlot(new SimpleContainer(1), 0, 106, 48));

        this.onChanged();
        this.addSlotListener(this);
    }

    @Override
    protected int get_te_inventory_slot_count() {
        return 3;
    }

    protected void addMachine() {
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 8, 48));
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 44, 48));
        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 62, 48));
    }

    protected void onChanged() {
        ItemStack resultItem = blockEntity.getResult();
        this.resultSlot.set(resultItem);
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
    protected boolean moveItemToActiveSlot(ItemStack stack) {
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
}