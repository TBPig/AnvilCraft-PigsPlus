package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.WirelessTransmitterBlock;
import dev.anvilcraft.pigsplus.block.handler.WirelessTransmitterFluidHandler;
import dev.anvilcraft.pigsplus.block.handler.WirelessTransmitterItemHandler;
import dev.anvilcraft.pigsplus.config.AddonServerConfig;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class WirelessTransmitterBlockEntity extends BlockEntity implements IPowerConsumer {
    public static final int TARGET_VALID = 0;
    public static final int TARGET_TOO_FAR = 1;
    public static final int TARGET_NOT_LOADED = 2;
    public static final int TARGET_IS_AIR = 3;
    public static final int TARGET_IS_SELF = 4;
    public static final int TARGET_INVALID = 5;

    private static final int PARTICLE_COOLDOWN = 8;
    private static final int MAX_PARTICLES = 16;
    private static final float PARTICLE_OFFSET = 0.25F;
    @Nullable
    private PowerGrid grid;
    @Nullable
    private BlockPos targetPos;
    private int transferCooldown = 0;
    private int particleCooldown = 0;
    private boolean particleRequested = false;
    @Getter
    private final WirelessTransmitterItemHandler itemHandler;
    @Getter
    private final WirelessTransmitterFluidHandler fluidHandler;

    public WirelessTransmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.itemHandler = new WirelessTransmitterItemHandler(this);
        this.fluidHandler = new WirelessTransmitterFluidHandler(this);
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
        if (this.getBlockPos().getCenter().distanceTo(pos.getCenter()) > this.getConfig().maxDistance) {
            return TARGET_TOO_FAR;
        }
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

    public boolean isBaseSide(@Nullable Direction side) {
        if (side == null) return false;
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof WirelessTransmitterBlock)) return false;
        return side == state.getValue(WirelessTransmitterBlock.FACING).getOpposite();
    }

    public ItemStack sendToTarget(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        IItemHandler target = this.getTargetItemHandler();
        if (target == null) return stack;
        ItemStack remainder = ItemHandlerUtil.insertItem(target, stack, simulate);
        if (!simulate && remainder.getCount() < stack.getCount()) {
            this.particleRequested = true;
        }
        return remainder;
    }

    public int sendToTarget(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty()) return 0;
        IFluidHandler target = this.getTargetFluidHandler();
        if (target == null) return 0;
        int filled = target.fill(resource, action);
        if (action.execute() && filled > 0) {
            this.particleRequested = true;
        }
        return filled;
    }

    @Nullable
    private IItemHandler getTargetItemHandler() {
        if (this.level == null || this.targetPos == null) return null;
        if (!this.level.isLoaded(this.targetPos)) return null;
        return this.level.getCapability(Capabilities.ItemHandler.BLOCK, this.targetPos, null);
    }

    @Nullable
    private IFluidHandler getTargetFluidHandler() {
        if (this.level == null || this.targetPos == null) return null;
        if (!this.level.isLoaded(this.targetPos)) return null;
        return this.level.getCapability(Capabilities.FluidHandler.BLOCK, this.targetPos, null);
    }

    private void tickParticles(ServerLevel level) {
        if (this.particleCooldown > 0) {
            this.particleCooldown--;
            return;
        }
        if (!this.particleRequested) return;
        this.particleRequested = false;

        BlockPos target = this.targetPos;
        if (target == null) return;
        Vec3 start = this.getBlockPos().getCenter();
        Vec3 end = target.getCenter();
        double distance = start.distanceTo(end);
        if (distance <= 0) return;

        int particleCount = Math.min(Mth.ceil(distance), MAX_PARTICLES);
        for (int i = 0; i < particleCount; i++) {
            Vec3 pos = start.lerp(end, level.getRandom().nextDouble()).offsetRandom(level.getRandom(), PARTICLE_OFFSET);
            level.sendParticles(ParticleTypes.PORTAL, pos.x(), pos.y(), pos.z(), 1, 0, 0, 0, 0);
        }
        this.particleCooldown = PARTICLE_COOLDOWN;
    }

    public void tick(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        this.flushState(level, pos);
        this.tickParticles(serverLevel);
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
        boolean itemsTransferred = this.transferItems(level, sourcePos, this.targetPos);
        boolean fluidTransferred = this.transferFluid(level, sourcePos, this.targetPos);
        if (itemsTransferred || fluidTransferred) {
            this.particleRequested = true;
            this.transferCooldown = AnvilCraft.CONFIG.machines.chuteMaxCooldown;
            this.setChanged();
        }
    }

    private boolean transferItems(Level level, BlockPos sourcePos, BlockPos targetPos) {
        IItemHandler source = level.getCapability(Capabilities.ItemHandler.BLOCK, sourcePos, this.getFacing());
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, null);
        if (source == null || target == null) return false;
        return ItemHandlerUtil.exportToTarget(source, this.getConfig().itemTransferAmount, stack -> true, target);
    }

    private boolean transferFluid(Level level, BlockPos sourcePos, BlockPos targetPos) {
        IFluidHandler source = level.getCapability(Capabilities.FluidHandler.BLOCK, sourcePos, this.getFacing());
        IFluidHandler target = level.getCapability(Capabilities.FluidHandler.BLOCK, targetPos, null);
        if (source == null || target == null) return false;

        FluidStack drained = source.drain(this.getConfig().fluidTransferAmount, IFluidHandler.FluidAction.SIMULATE);
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

    private AddonServerConfig.WirelessTransmitter getConfig() {
        return AnvilCraftPigsPlus.CONFIG.wirelessTransmitter;
    }

    @Override
    public int getInputPower() {
        return this.getConfig().power;
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
