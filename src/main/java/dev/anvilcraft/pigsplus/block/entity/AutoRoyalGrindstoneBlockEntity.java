package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.AutoRoyalGrindstoneMenu;
import dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

import static dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu.DEFAULT_REPAIR_MATERIAL;
import static dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu.GOLD_PER_CURSE;
import static dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu.REPAIR_COST_RECIPES;

@Getter
public class AutoRoyalGrindstoneBlockEntity extends AutoMachineBlockEntity {
    @Getter
    private ItemStack resultToolStack = ItemStack.EMPTY;
    @Getter
    private ItemStack resultMaterialStack = ItemStack.EMPTY;
    private int usedMaterialCount = 0;

    public AutoRoyalGrindstoneBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 2);
    }

    @Override
    protected ItemStacksResourceHandler createItemHandler(int size) {
        return new ItemStacksResourceHandler(size) {

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
                if (index == 0 && REPAIR_COST_RECIPES.containsKey(resource.getItem())) return amount;
                if (index == 1 && !REPAIR_COST_RECIPES.containsKey(resource.getItem())) return amount;
                return super.insert(index, resource, amount, transaction);
            }

            @Override
            protected void onContentsChanged(int slot, ItemStack stack) {
                calcResult();
                setChanged();
            }
        };
    }

    private void calcResult() {
        resultToolStack = ItemStack.EMPTY;
        resultMaterialStack = ItemStack.EMPTY;
        usedMaterialCount = 0;
        if (level == null) return;

        ItemStack toolStack = itemHandler.getResource(0).toStack(itemHandler.getAmountAsInt(0));
        ItemStack materialStack = itemHandler.getResource(1).toStack(itemHandler.getAmountAsInt(1));

        if (toolStack.isEmpty() || materialStack.isEmpty()) return;

        RoyalGrindstoneMenu.RepairCostRecipeEntry recipe = REPAIR_COST_RECIPES.getOrDefault(materialStack.getItem(), null);

        // 计算附魔惩罚可消耗的金材料
        int repairCost = toolStack.getOrDefault(DataComponents.REPAIR_COST, 0);
        usedMaterialCount = Math.min(materialStack.getCount(), repairCost / recipe.count());
        resultToolStack = toolStack.copy();
        resultToolStack.setCount(1);
        int remainMaterialCount = materialStack.getCount();
        if (usedMaterialCount > 0) {
            int materialAbility = usedMaterialCount * recipe.count();
            int remainRepairCost = repairCost - materialAbility;
            remainMaterialCount -= usedMaterialCount;
            resultToolStack.set(DataComponents.REPAIR_COST, remainRepairCost);
            resultMaterialStack = recipe.item().getDefaultInstance();
            resultMaterialStack.setCount(usedMaterialCount);
        }

        // 计算诅咒附魔可消耗的金材料
        if (!materialStack.is(DEFAULT_REPAIR_MATERIAL)) return;
        if (remainMaterialCount < GOLD_PER_CURSE) return;
        DataComponentType<ItemEnchantments> enchantmentComponent =
            resultToolStack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
        ItemEnchantments enchantments = resultToolStack.get(enchantmentComponent);
        if (enchantments == null) return;

        ItemEnchantments.Mutable mutEnch = new ItemEnchantments.Mutable(enchantments);
        Iterator<Holder<Enchantment>> iterator = mutEnch.keySet().iterator();

        // 逐个去除诅咒附魔
        while (iterator.hasNext() && remainMaterialCount >= GOLD_PER_CURSE) {
            Holder<Enchantment> curseEnchantment = iterator.next();
            if (!curseEnchantment.is(EnchantmentTags.CURSE)) continue;
            iterator.remove();
            usedMaterialCount += GOLD_PER_CURSE;
            remainMaterialCount -= GOLD_PER_CURSE;
        }
        resultMaterialStack = recipe.item().getDefaultInstance();
        resultMaterialStack.setCount(usedMaterialCount);
        resultToolStack.set(enchantmentComponent, mutEnch.toImmutable());
        if (resultToolStack.is(Items.ENCHANTED_BOOK) && !EnchantmentHelper.hasAnyEnchantments(resultToolStack)) {
            resultToolStack = resultToolStack.transmuteCopy(Items.BOOK);
        }
    }

    @Override
    protected boolean work(Level level) {
        if (getGrid() == null || !getGrid().isWorking()) return false;
        calcResult();
        if (resultToolStack.isEmpty()) return false;

        if (!exportItem(resultToolStack, List.of(resultMaterialStack))) return false;

        // 消耗输入物品
        if (itemHandler.getAmountAsInt(1) > 0) {
            try (Transaction root = Transaction.openRoot()) {
                itemHandler.extract(1, itemHandler.getResource(1), this.usedMaterialCount, root);
                root.commit();
            }
        }
        if (itemHandler.getAmountAsInt(0) > 0) {
            try (Transaction root = Transaction.openRoot()) {
                itemHandler.extract(0, itemHandler.getResource(0), 1, root);
                root.commit();
            }
        }
        level.updateNeighborsAt(getBlockPos(), AddonBlocks.AUTO_ROYAL_GRINDSTONE_BLOCK.get());
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("HasResultItemStack", !this.resultToolStack.isEmpty());
        if (!this.resultToolStack.isEmpty()) {
            output.store("ResultItemStack", ItemStack.OPTIONAL_CODEC, this.resultToolStack);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (input.getBooleanOr("HasResultItemStack", false)) {
            this.resultToolStack = input.read("ResultItemStack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (player.isSpectator()) return null;
        return new AutoRoyalGrindstoneMenu(AddonMenuTypes.AUTO_ROYAL_GRINDSTONE.get(), i, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.anvilcraft_pigsplus.auto_royal_grindstone");
    }

    @Override
    public Block getBlock() {
        return AddonBlocks.AUTO_ROYAL_GRINDSTONE_BLOCK.get();
    }
}