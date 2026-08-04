package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.WirelessTransmitterBlock;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class WirelessTransmitterBlockEntity extends BlockEntity implements IPowerConsumer {
    public static final int CONSUMED_POWER = 64;
    public static final int TARGET_VALID = 0;
    public static final int TARGET_TOO_FAR = 1;
    public static final int TARGET_NOT_LOADED = 2;
    public static final int TARGET_IS_AIR = 3;
    public static final int TARGET_IS_SELF = 4;
    public static final int TARGET_INVALID = 5;
    private static final int ITEM_TRANSFER_AMOUNT = 64;
    private static final int FLUID_TRANSFER_AMOUNT = 8000;
    private static final int MAX_DISTANCE = 64;

    @Nullable
    private PowerGrid grid;
    @Nullable
    private BlockPos targetPos;
    private int transferCooldown = 0;

    public WirelessTransmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.targetPos = tag.contains("TargetPos") ? BlockPos.of(tag.getLong("TargetPos")) : null;
        this.transferCooldown = tag.getInt("TransferCooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.targetPos != null) {
            tag.putLong("TargetPos", this.targetPos.asLong());
        }
        tag.putInt("TransferCooldown", this.transferCooldown);
    }

    public int setTargetPos(BlockPos pos) {
        if (this.level == null) return TARGET_INVALID;
        if (pos.equals(this.getBlockPos())) return TARGET_IS_SELF;
        if (this.getBlockPos().getCenter().distanceTo(pos.getCenter()) > MAX_DISTANCE) return TARGET_TOO_FAR;
        if (!this.level.isLoaded(pos)) return TARGET_NOT_LOADED;
        if (this.level.getBlockState(pos).isAir()) return TARGET_IS_AIR;
        this.targetPos = pos.immutable();
        this.setChanged();
        return TARGET_VALID;
    }

    @Nullable
    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    public void tick(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel)) return;
        this.flushState(level, pos);
        if (this.transferCooldown > 0) {
            this.transferCooldown--;
            this.setChanged();
            return;
        }
        if (this.targetPos == null) return;

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof WirelessTransmitterBlock)) return;
        if (state.getValue(WirelessTransmitterBlock.OVERLOAD)) return;
        if (!level.isLoaded(this.targetPos)) return;

        BlockPos sourcePos = pos.relative(state.getValue(WirelessTransmitterBlock.FACING).getOpposite());
        if (this.targetPos.equals(sourcePos)) return;
        boolean transferred = this.transferItems(level, sourcePos, this.targetPos);
        transferred |= this.transferFluid(level, sourcePos, this.targetPos);
        if (transferred) {
            this.transferCooldown = AnvilCraft.CONFIG.chuteMaxCooldown;
            this.setChanged();
        }
    }

    private boolean transferItems(Level level, BlockPos sourcePos, BlockPos targetPos) {
        IItemHandler source = level.getCapability(Capabilities.ItemHandler.BLOCK, sourcePos, this.getFacing());
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, null);
        if (source == null || target == null) return false;
        return ItemHandlerUtil.exportToTarget(source, ITEM_TRANSFER_AMOUNT, stack -> true, target);
    }

    private boolean transferFluid(Level level, BlockPos sourcePos, BlockPos targetPos) {
        IFluidHandler source = level.getCapability(Capabilities.FluidHandler.BLOCK, sourcePos, this.getFacing());
        IFluidHandler target = level.getCapability(Capabilities.FluidHandler.BLOCK, targetPos, null);
        if (source == null || target == null) return false;

        FluidStack drained = source.drain(FLUID_TRANSFER_AMOUNT, IFluidHandler.FluidAction.SIMULATE);
        if (drained.isEmpty()) return false;
        int accepted = target.fill(drained, IFluidHandler.FluidAction.SIMULATE);
        if (accepted <= 0) return false;

        FluidStack actual = source.drain(drained.copyWithAmount(accepted), IFluidHandler.FluidAction.EXECUTE);
        if (actual.isEmpty()) return false;
        int filled = target.fill(actual, IFluidHandler.FluidAction.EXECUTE);
        if (filled < actual.getAmount()) {
            source.fill(actual.copyWithAmount(actual.getAmount() - filled), IFluidHandler.FluidAction.EXECUTE);
        }
        return filled > 0;
    }

    private Direction getFacing() {
        if (this.level == null) return Direction.NORTH;
        BlockState state = this.level.getBlockState(this.getBlockPos());
        if (!(state.getBlock() instanceof WirelessTransmitterBlock)) return Direction.NORTH;
        return state.getValue(WirelessTransmitterBlock.FACING);
    }

    @Override
    public int getInputPower() {
        return CONSUMED_POWER;
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return this.level;
    }

    @Override
    public BlockPos getPos() {
        return this.getBlockPos();
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return this.grid;
    }
}
