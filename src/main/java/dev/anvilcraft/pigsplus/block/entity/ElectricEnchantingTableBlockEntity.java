package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.ElectricEnchantingTableBlock;
import dev.anvilcraft.pigsplus.util.ChiseledBookShelfUtil;
import dev.anvilcraft.pigsplus.util.ExpUtil;
import dev.anvilcraft.pigsplus.util.FluidUtil;
import dev.anvilcraft.pigsplus.util.ParticleUtil;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.itemhandler.FilteredItemStackHandler;
import dev.dubhe.anvilcraft.api.itemhandler.IItemHandlerHolder;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.entity.IFilterBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModFluidTags;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.redstone.Redstone;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.CONFIG;

public class ElectricEnchantingTableBlockEntity extends BlockEntity
    implements IPowerConsumer, IFilterBlockEntity, IItemHandlerHolder {
    public Map<Holder<Enchantment>, Integer> enchantments = new HashMap<>();
    @Getter
    private int needXpLiquid = 0;
    @Getter
    private int absorbedXpLiquid = 0;
    @Getter
    private double decreaseRate = 1.0;
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
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

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
        tag.putDouble("decreaseRate", this.decreaseRate);
        tag.putInt("needXpLiquid", this.needXpLiquid);
        tag.putInt("absorbedXpLiquid", this.absorbedXpLiquid);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        itemHandler.deserializeNBT(provider, tag.getCompound("Depository"));
        this.decreaseRate = tag.getDouble("decreaseRate");
        this.needXpLiquid = tag.getInt("needXpLiquid");
        this.absorbedXpLiquid = tag.getInt("absorbedXpLiquid");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void gridTick() {
        if (level == null || level.isClientSide()) return;
        this.decreaseRate = calcDecreaseRate();
    }

    /**
     * 电力附魔台逻辑
     */
    public void tick(Level level1, BlockPos blockPos) {
        this.flushState(level1, blockPos);
        if (!(level1 instanceof ServerLevel serverLevel)) return;
        if (grid == null || !grid.isWorking()) return;
        if (level1.getBlockState(blockPos).getValue(ElectricEnchantingTableBlock.POWERED)) return;

        if (!itemHandler.getStackInSlot(0).isEmpty() && itemHandler.getStackInSlot(1).isEmpty()) {
            this.input();
        }

        if (this.isEnchanting() && !itemHandler.getStackInSlot(1).isEmpty()) {
            this.absorbXP(serverLevel);
            this.tryEnchant(serverLevel);
        }

        if (!this.isEnchanting() && !itemHandler.getStackInSlot(1).isEmpty() && itemHandler.getStackInSlot(2).isEmpty()) {
            this.output();
        }

        int signal = this.getAnalogRedstoneSignal();
        if (this.signalCache != signal) {
            this.signalCache = signal;
            level1.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        }
    }

    protected void input() {
        itemHandler.setStackInSlot(1, itemHandler.getStackInSlot(0).copy());
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        this.refresh();
    }

    protected void refresh() {
        this.enchantments = getEnchantment();
        this.decreaseRate = calcDecreaseRate();
        this.needXpLiquid = (int) Math.ceil(calcCostXpLiqiud(this.enchantments) * this.decreaseRate);
    }

    protected void absorbXP(ServerLevel level) {
        for (BlockPos blockPos : ElectricEnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos blockPos1 = getBlockPos().offset(blockPos);
            if (!(level.getBlockEntity(blockPos1) instanceof ExperienceInterfaceBlockEntity xpInterface)) continue;
            IFluidHandler handler = xpInterface.getHandler();
            if (handler == null) continue;

            int onceNeedXpLiquid = this.needXpLiquid - this.absorbedXpLiquid;
            onceNeedXpLiquid = Math.clamp(onceNeedXpLiquid, 0, CONFIG.electricEnchantingTable.fluidComsumeSpeed);

            FluidStack accepted = FluidUtil.drain(handler, ModFluidTags.EXPERIENCE, onceNeedXpLiquid, IFluidHandler.FluidAction.EXECUTE);

            if (accepted.getAmount() > 0) {
                ParticleUtil.sendParticle(level, blockPos1, this.getBlockPos());
                this.absorbedXpLiquid += accepted.getAmount();
                this.setChanged();
            }

        }
    }

    protected void tryEnchant(ServerLevel level) {
        if (this.absorbedXpLiquid < this.needXpLiquid) return;
        this.refresh();

        if (this.absorbedXpLiquid < this.needXpLiquid) return;
        this.absorbedXpLiquid -= this.needXpLiquid;
        this.needXpLiquid = 0;
        level.playSound(
            null,
            getBlockPos(),
            SoundEvents.ENCHANTMENT_TABLE_USE,
            SoundSource.BLOCKS,
            1.0F,
            level.getRandom().nextFloat() * 0.1F + 0.9F
        );


        ItemStack stack = itemHandler.getStackInSlot(1);
        if (stack.isEmpty()) return;

        ItemStack transformed = enchant(stack);
        itemHandler.setStackInSlot(1, transformed);
    }

    protected double calcDecreaseRate() {
        if (level == null) return 0;
        float enchantPower = 0;
        for (BlockPos blockPos : ElectricEnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos bookshelfPos = getBlockPos().offset(blockPos);
            enchantPower += level.getBlockState(bookshelfPos).getEnchantPowerBonus(level, bookshelfPos);
        }
        return 1 / (1 + CONFIG.electricEnchantingTable.decreaseRate * enchantPower);
    }

    protected Map<Holder<Enchantment>, Integer> getEnchantment() {
        Map<Holder<Enchantment>, Integer> enchantments = new HashMap<>();
        if (level == null) return enchantments;
        List<Object2IntMap.Entry<Holder<Enchantment>>> enchants = ChiseledBookShelfUtil.countEnchantmentsInArea(
            level,
            getBlockPos(),
            ElectricEnchantingTableBlock.BOOKSHELF_OFFSETS
        );

        // 遍历所有附魔并保留最高等级
        for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchants) {
            int currentLevel = enchantment.getIntValue();
            Holder<Enchantment> enchantmentType = enchantment.getKey();
            enchantments.merge(enchantmentType, currentLevel, Math::max);
        }
        return enchantments;
    }

    public static int calcCostXpLiqiud(Map<Holder<Enchantment>, Integer> enchantments) {
        return calcCostXp(enchantments) * ExpUtil.EXPERIENCE_TO_LIQUID;

    }

    public static int calcCostXp(Map<Holder<Enchantment>, Integer> enchantments) {
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
        return ExpUtil.getXpfromAllLevel(xpLevelCost);
    }

    protected ItemStack enchant(ItemStack stack) {
        ItemStack result = stack.copy();
        if (enchantments.isEmpty()) return result;

        if (result.is(Items.BOOK)) {
            result = stack.transmuteCopy(Items.ENCHANTED_BOOK);
        }

        ItemEnchantments.Mutable resultEnchantments =
            new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
        for (Map.Entry<Holder<Enchantment>, Integer> entry : enchantments.entrySet()) {

            Holder<Enchantment> enchantment = entry.getKey();
            int newLevel = entry.getValue();
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

    protected void output() {
        itemHandler.setStackInSlot(2, itemHandler.getStackInSlot(1).copy());
        itemHandler.setStackInSlot(1, ItemStack.EMPTY);
    }

    public ItemStack getDisplayItemStack() {
        for (int i = 2; i >= 0; i--) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return itemHandler.getStackInSlot(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }

    @Override
    public int getInputPower() {
        return getBlockState().getValue(ElectricEnchantingTableBlock.POWERED) ? 0 : CONFIG.electricEnchantingTable.power;
    }

    public double getProgress() {
        if (needXpLiquid > 0) return (double) absorbedXpLiquid / needXpLiquid;
        return 0;
    }

    public int getAnalogRedstoneSignal() {
        if (itemHandler.getStackInSlot(0).isEmpty() && itemHandler.getStackInSlot(1).isEmpty()) return 0;
        return (int) Math.round(getProgress() * Redstone.SIGNAL_MAX);
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
        return this.isEnchanting();
    }

    public boolean isEnchanting() {
        return this.needXpLiquid > 0;
    }
}
