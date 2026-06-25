package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.ElectricEnchantingTableBlock;
import dev.anvilcraft.pigsplus.init.AddonParticleTypes;
import dev.anvilcraft.pigsplus.util.ChiseledBookShelfUtil;
import dev.anvilcraft.pigsplus.util.ExpUtil;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.IHasDisplayItem;
import dev.dubhe.anvilcraft.api.itemhandler.FilteredItemStackHandler;
import dev.dubhe.anvilcraft.api.itemhandler.IItemResourceHandlerHolder;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.entity.IFilterBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModFluids;
import dev.dubhe.anvilcraft.network.UpdateDisplayItemPacket;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.CONFIG;

public class ElectricEnchantingTableBlockEntity extends BlockEntity
    implements IPowerConsumer, IFilterBlockEntity, IItemResourceHandlerHolder, IHasDisplayItem {
    public static final double PARTICLE_SPEED = 0.06;
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
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            if (!this.getResource(0).isEmpty()) return 0;
            return super.insert(0, resource, 1, transaction);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (index != 0) return 0;
            return super.insert(index, resource, amount, transaction);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return EnchantmentHelper.canStoreEnchantments(resource.toStack());
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return super.extract(2, resource, amount, transaction);
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (index != 2) return 0;
            return super.extract(2, resource, amount, transaction);
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            if (level != null && !level.isClientSide()) {
                setChanged();
                updateDisplayItemStack();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }

    };
    @Getter
    private ItemStack displayItemStack = ItemStack.EMPTY;

    @Getter
    @Setter
    private @Nullable PowerGrid grid;

    public ElectricEnchantingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output.child("Depository"));
        output.putDouble("decreaseRate", this.decreaseRate);
        output.putInt("absorbedXpLiquid", this.absorbedXpLiquid);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemHandler.deserialize(input.childOrEmpty("Depository"));
        this.decreaseRate = input.getDoubleOr("decreaseRate", 1);
        this.absorbedXpLiquid = input.getIntOr("absorbedXpLiquid", 0);
    }

    @Override
    public void gridTick() {
        if (level == null || level.isClientSide()) return;
        this.decreaseRate = calcPowerRate();
    }

    /**
     * 电力附魔台逻辑
     */
    public void tick(Level level1, BlockPos blockPos) {
        this.flushState(level1, blockPos);
        if (!(level1 instanceof ServerLevel serverLevel)) return;
        if (grid == null || !grid.isWorking()) return;
        if (level1.getBlockState(blockPos).getValue(ElectricEnchantingTableBlock.POWERED)) return;

        tryInput();
        tryEnchant(serverLevel);
        tryFinish(serverLevel);
        tryOutput();

        int signal = this.getAnalogRedstoneSignal();
        if (this.signalCache != signal) {
            this.signalCache = signal;
            level1.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        }
    }

    protected void tryInput() {
        if (!itemHandler.getResource(1).isEmpty()) return;
        ItemResource resource = itemHandler.getResource(0);
        if (resource.isEmpty()) return;

        itemHandler.set(0, ItemResource.EMPTY, 0);
        this.enchantments = getEnchantment();
        this.decreaseRate = calcPowerRate();
        this.needXpLiquid = (int) Math.ceil(CalcCostPowerValue(this.enchantments) * this.decreaseRate);
        itemHandler.set(1, resource, 1);
    }

    protected double calcPowerRate() {
        if (level == null) return 0;
        float enchantPower = 0;
        for (BlockPos blockPos : ElectricEnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos bookshelfPos = getBlockPos().offset(blockPos);
            enchantPower += level.getBlockState(bookshelfPos).getEnchantPowerBonus(level, bookshelfPos);
        }
        return Math.pow(1 - CONFIG.electricEnchantingTable.decreaseRate, enchantPower);
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

    public static int CalcCostPowerValue(Map<Holder<Enchantment>, Integer> enchantments) {
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
        return ExpUtil.getXpfromAllLevel(xpLevelCost - 1);
    }

    protected void tryEnchant(ServerLevel level) {
        if (itemHandler.getResource(1).isEmpty()) return;
        if (!isGridWorking()) return;
        if (absorbedXpLiquid >= needXpLiquid) return;

        int onceAbsorbedXpLiquid = 0;
        for (BlockPos blockPos : ElectricEnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos blockPos1 = getBlockPos().offset(blockPos);
            if (!(level.getBlockEntity(blockPos1) instanceof ExperienceInterfaceBlockEntity xpInterface)) continue;
            ResourceHandler<FluidResource> handler = xpInterface.getHandler();
            if (handler == null) continue;

            int onceNeedXpLiquid = this.needXpLiquid - this.absorbedXpLiquid;
            onceNeedXpLiquid = Math.clamp(onceNeedXpLiquid, 0, CONFIG.electricEnchantingTable.fluidComsumeSpeed);
            onceNeedXpLiquid -= onceAbsorbedXpLiquid;

            try (Transaction transaction = Transaction.openRoot()) {
                int accepted = handler.extract(FluidResource.of(ModFluids.EXP_FLUID), onceNeedXpLiquid, transaction);
                onceAbsorbedXpLiquid += accepted;
                if (accepted > 0) {
                    level.sendParticles(
                        AddonParticleTypes.EXP.get(),
                        (double) blockPos1.getX() + (double) 0.5F,
                        (double) blockPos1.getY() + (double) 0.5F,
                        (double) blockPos1.getZ() + (double) 0.5F,
                        0,
                        blockPos.getX() * -PARTICLE_SPEED * (level.getRandom().nextFloat() * 0.2 + 0.9),
                        blockPos.getY() * -PARTICLE_SPEED * (level.getRandom().nextFloat() * 0.2 + 0.9),
                        blockPos.getZ() * -PARTICLE_SPEED * (level.getRandom().nextFloat() * 0.2 + 0.9),
                        1.0
                    );

                }
                transaction.commit();
            }

        }
        this.absorbedXpLiquid += onceAbsorbedXpLiquid;
    }

    protected void tryFinish(ServerLevel level) {
        if (itemHandler.getResource(1).isEmpty()) return;

        if (this.absorbedXpLiquid < this.needXpLiquid) return;
        this.enchantments = getEnchantment();
        this.decreaseRate = calcPowerRate();
        this.needXpLiquid = (int) Math.ceil(CalcCostPowerValue(this.enchantments) * this.decreaseRate);

        if (this.absorbedXpLiquid < this.needXpLiquid) return;
        if (this.needXpLiquid == 0) return;
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


        ItemResource resource = itemHandler.getResource(1);
        if (resource.isEmpty()) return;

        ItemStack transformed = enchant(resource.toStack());
        itemHandler.set(1, ItemResource.of(transformed), 1);

    }

    protected void tryOutput() {
        if (!itemHandler.getResource(2).isEmpty()) return;
        ItemResource resource = itemHandler.getResource(1);
        if (resource.isEmpty()) return;
        if (this.isEnchanting()) return;

        itemHandler.set(2, resource, 1);
        itemHandler.set(1, ItemResource.EMPTY, 0);
    }

    protected ItemStack enchant(ItemStack stack) {
        ItemStack result = stack.copy();
        if (result.is(Items.BOOK)) result = stack.transmuteCopy(Items.ENCHANTED_BOOK);

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

    private void updateDisplayItemStack() {
        ItemStack newDisplayStack = getDisplayItemStackForRender();
        for (int i = 2; i >= 0; i--) {
            if (!itemHandler.getResource(i).isEmpty()) {
                newDisplayStack = itemHandler.getResource(i).toStack();
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
            if (!itemHandler.getResource(i).isEmpty()) {
                return itemHandler.getResource(i).toStack();
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
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }

    @Override
    public int getInputPower() {
        return CONFIG.electricEnchantingTable.power;
    }

    public double getProgress() {
        if (needXpLiquid > 0) return (double) absorbedXpLiquid / needXpLiquid;
        return 0;
    }

    public int getAnalogRedstoneSignal() {
        if (itemHandler.getResource(0).isEmpty() && itemHandler.getResource(1).isEmpty()) return 0;
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
        return needXpLiquid > 0;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        Containers.dropContents(this.level, pos, this.getFilteredItemStackHandler().getStacks());
    }

    public boolean isEnchanting() {
        return this.needXpLiquid > 0;
    }
}
