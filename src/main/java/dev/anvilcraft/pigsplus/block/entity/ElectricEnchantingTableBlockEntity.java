package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.util.BlockUtil;
import dev.anvilcraft.pigsplus.util.ChiseledBookShelfUtil;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.IHasDisplayItem;
import dev.dubhe.anvilcraft.api.itemhandler.FilteredItemStackHandler;
import dev.dubhe.anvilcraft.api.itemhandler.IItemHandlerHolder;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.ChargerBlock;
import dev.dubhe.anvilcraft.block.entity.IFilterBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.network.UpdateDisplayItemPacket;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectricEnchantingTableBlockEntity extends BlockEntity
    implements IPowerConsumer, IFilterBlockEntity, IItemHandlerHolder, IHasDisplayItem {
    public final int MAX_WORK_TIME = 10 * 20;
    public final int POWER_PER_LEVEL = 128;
    public final int POWER_PER_PER_LEVEL = 16;
    public final double DECREASE_POWER_PER_BOOKSHELF = 0.02;
    public final int STD_POWER_VALUE = 1024;
    public final int POWER_ADDITION_PER_CORE = 256;
    public Map<Holder<Enchantment>, Integer> enchantments = new HashMap<>();
    private int time = 0;
    private int maxPowerValue = 0;
    private int powerValue = 0;
    private double powerRate = 1;
    private int signalCache = 0;
    @Getter
    private final FilteredItemStackHandler itemHandler = new FilteredItemStackHandler(3) {

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 0 && itemHandler.getStackInSlot(0).isEmpty()) {
                ItemStack original = stack.copy();
                original.shrink(1);
                if (original.isEmpty()) {
                    return super.insertItem(slot, stack.copyWithCount(1), simulate);
                } else {
                    ItemStack left = super.insertItem(slot, stack.copyWithCount(1), simulate);
                    return stack.copyWithCount(stack.getCount() - 1 + left.getCount());
                }
            } else {
                return stack;
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return EnchantmentHelper.canStoreEnchantments(stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 2 ? super.extractItem(2, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (level != null && !level.isClientSide) {
                setChanged();
                updateDisplayItemStack();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    @Getter
    private ItemStack displayItemStack = ItemStack.EMPTY;

    @Getter
    @Setter
    private PowerGrid grid;

    public ElectricEnchantingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Depository", itemHandler.serializeNBT(provider));
        tag.putInt("time", time);
        tag.putInt("powerValue", powerValue);
        tag.putInt("maxPowerValue", maxPowerValue);
        tag.putDouble("powerRate", powerRate);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        itemHandler.deserializeNBT(provider, tag.getCompound("Depository"));
        time = tag.getInt("time");
        powerValue = tag.getInt("powerValue");
        maxPowerValue = tag.getInt("maxPowerValue");
        powerRate = tag.getDouble("powerRate");
    }

    private void dropItemStack(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (level != null) {
                Vec3 dropPos = getBlockPos().above().getBottomCenter();
                ItemEntity itemEntity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z,
                    stack, 0, 0, 0
                );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
    }

    @Override
    public void gridTick() {
        if (level == null || level.isClientSide()) return;
        maxPowerValue = getMaxPowerValue();
        powerRate = getPowerRate();
        selectEnchantment();
    }

    /**
     * 电力附魔台逻辑
     */
    public void tick(Level level1, BlockPos blockPos) {
        this.flushState(level1, blockPos);
        if (grid == null) return;
        if (level1.getBlockState(blockPos).getValue(ChargerBlock.POWERED)) return;

        if (time == 0) moveItemToTransformingSlot();
        if (time > 0 && isGridWorking()) time--;
        if (time == 0) moveItemToTransformedOverSlot();

        int signal = this.getAnalogRedstoneSignal();
        if (this.signalCache != signal) {
            this.signalCache = signal;
            level1.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        }
    }

    private void moveItemToTransformingSlot() {
        ItemStack stack = itemHandler.getStackInSlot(0).copy();
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        if (stack.isEmpty()) return;
        if (!itemHandler.getStackInSlot(1).isEmpty()) return;

        maxPowerValue = getMaxPowerValue();
        powerRate = getPowerRate();
        selectEnchantment();

        int needPower = getPowerValue();
        if (needPower > maxPowerValue | needPower <= 0) {
            dropItemStack(stack);
            return;
        }
        itemHandler.setStackInSlot(1, stack);
        powerValue = needPower;
        time = MAX_WORK_TIME;
    }

    private int getMaxPowerValue() {
        if (level == null) return STD_POWER_VALUE;
        int coreCount = BlockUtil.countBlocks(level, getBlockPos(), 2, ModBlocks.MAGNETO_ELECTRIC_CORE_BLOCK);
        return POWER_ADDITION_PER_CORE * coreCount + STD_POWER_VALUE;
    }

    private double getPowerRate() {
        if (level == null) return 0;
        int bookshelfCount = BlockUtil.countBlocks(level, getBlockPos(), 2, Blocks.BOOKSHELF);
        return Math.pow(1 - DECREASE_POWER_PER_BOOKSHELF, bookshelfCount);
    }

    private void selectEnchantment() {
        if (level == null) return;
        List<Object2IntMap.Entry<Holder<Enchantment>>> enchants = ChiseledBookShelfUtil.countEnchantmentsInArea(level, getBlockPos(), 2);

        // 遍历所有附魔并保留最高等级
        enchantments = new HashMap<>();
        for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchants) {
            int currentLevel = enchantment.getIntValue();
            Holder<Enchantment> enchantmentType = enchantment.getKey();
            enchantments.merge(enchantmentType, currentLevel, Math::max);
        }
    }

    private int getPowerValue() {
        int xpLevelCost = 0;
        for (Map.Entry<Holder<Enchantment>, Integer> entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantmentType = entry.getKey();
            int anvilCost = enchantmentType.value().getAnvilCost();
            int level = entry.getValue();
            xpLevelCost += anvilCost * level;

            // 负值可能是数据溢出，设为最大值
            if (xpLevelCost < 0) {
                xpLevelCost = Integer.MAX_VALUE;
                break;
            }
        }
        xpLevelCost /= 2;
        int powerFromEnchantments = xpLevelCost * (POWER_PER_LEVEL + xpLevelCost * POWER_PER_PER_LEVEL);
        return (int) Math.ceil(powerFromEnchantments * powerRate);
    }

    private void moveItemToTransformedOverSlot() {
        powerValue = 0;
        ItemStack stack = itemHandler.getStackInSlot(1).copy();
        if (stack.isEmpty()) return;
        if (!itemHandler.getStackInSlot(2).isEmpty()) return;

        ItemStack transformed = enchant(stack);
        itemHandler.setStackInSlot(2, transformed);
        itemHandler.setStackInSlot(1, ItemStack.EMPTY);
        if (level != null) {
            level.playSound(
                null,
                getBlockPos(),
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.BLOCKS,
                1.0F,
                level.random.nextFloat() * 0.1F + 0.9F
            );
        }
    }

    private ItemStack enchant(ItemStack stack) {
        ItemStack result = stack.copy();
        if (result.is(Items.BOOK)) result = stack.transmuteCopy(Items.ENCHANTED_BOOK);

        ItemEnchantments.Mutable resultEnchantments =
            new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
        for (Map.Entry<Holder<Enchantment>, Integer> entry : enchantments.entrySet()) {

            Holder<Enchantment> enchantment = entry.getKey();
            Integer newLevel = entry.getValue();
            int oldLevel = resultEnchantments.getLevel(enchantment);
            int combinedLevel = (oldLevel == newLevel) ? newLevel + 1 : Math.max(oldLevel, newLevel);
            if (!AnvilCraft.CONFIG.transcendenceAnvilBeyondMaxLevel && combinedLevel > enchantment.value().getMaxLevel()) {
                combinedLevel = enchantment.value().getMaxLevel();
            }

            resultEnchantments.set(enchantment, combinedLevel);
        }

        EnchantmentHelper.setEnchantments(result, resultEnchantments.toImmutable());
        return result;
    }

    private void updateDisplayItemStack() {
        ItemStack newDisplayStack = getDisplayItemStackForRender();
        for (int i = 2; i >= 0; i--) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                newDisplayStack = itemHandler.getStackInSlot(i);
                break;
            }
        }
        if (!ItemStack.matches(displayItemStack, newDisplayStack)) {
            displayItemStack = newDisplayStack.copy();
            PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                level.getChunk(getBlockPos()).getPos(),
                new UpdateDisplayItemPacket(displayItemStack, getPos())
            );
        }
    }

    private ItemStack getDisplayItemStackForRender() {
        for (int i = 2; i >= 0; i--) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return itemHandler.getStackInSlot(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void updateDisplayItem(ItemStack stack) {
        this.displayItemStack = stack;
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public Level getCurrentLevel() {
        return getLevel();
    }

    @Override
    public int getInputPower() {
        return !this.getBlockState().getValue(ChargerBlock.POWERED) ? powerValue : 0;
    }

    public double getProgress() {
        if (time > 0) return 1 - (double) time / MAX_WORK_TIME;
        return 0;
    }

    public int getAnalogRedstoneSignal() {
        if (itemHandler.getStackInSlot(0).isEmpty() && itemHandler.getStackInSlot(1).isEmpty()) return 0;
        return (int) Math.round(getProgress() * 15);
    }

    @Override
    public FilteredItemStackHandler getFilteredItemStackHandler() {
        return itemHandler;
    }

    @Override
    public boolean isFilterEnabled() {
        return true;
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return time > 0;
    }

}
