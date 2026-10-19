package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.BeamHeatConverterBlock;
import dev.anvilcraft.pigsplus.init.AddonHeaterInfos;
import dev.dubhe.anvilcraft.api.heat.HeaterManager;
import dev.dubhe.anvilcraft.block.entity.BaseLaserBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

/**
 * 光束变热器
 *
 * <p>每周期（{@value #CYCLE_TICKS} tick）在 1~{@value #MAX_CYCLE_INDEX} 中随机选取一个序号并保证与上一周期不同，
 * 当接收到的激光等级之和等于该序号时记为一个满足的周期。连续两个周期满足后，
 * 下一个周期进入工作状态，将自身为中心 3x3x3 的方块持续加热至红热。</p>
 */
public class BeamHeatConverterBlockEntity extends BaseLaserBlockEntity {
    public static final int CYCLE_TICKS = 100;
    public static final int SATISFIED_CYCLES_TO_WORK = 2;
    public static final int MAX_CYCLE_INDEX = 8;

    @Getter
    private int cycleIndex = 0;
    private int cycleTicks = 0;
    private int satisfiedStreak = 0;
    @Getter
    private boolean working = false;

    public BeamHeatConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected int getBaseLaserLevel() {
        return 0;
    }

    @Override
    public Direction getFacing() {
        return Direction.DOWN;
    }

    @Override
    public Set<Direction> getIgnoreFace() {
        return Set.of(Direction.UP);
    }

    @Override
    public void tick(Level level) {
        this.updateLaserLevel(this.calculateLaserLevel());
        if (level instanceof ServerLevel serverLevel) {
            this.serverTick(serverLevel);
        }
        super.tick(level);
        this.resetState();
    }

    private void serverTick(ServerLevel level) {
        this.cycleTicks++;
        if (this.cycleTicks >= CYCLE_TICKS) {
            this.cycleTicks = 0;
            boolean satisfied = this.isLaserRequirementMet();
            this.satisfiedStreak = satisfied ? this.satisfiedStreak + 1 : 0;
            this.setWorking(this.satisfiedStreak >= SATISFIED_CYCLES_TO_WORK, level);
            this.rollCycleIndex(level);
        }
        if (this.cycleIndex == 0) {
            this.rollCycleIndex(level);
        }
        if (!this.working) {
            return;
        }
        HeaterManager.addProducer(this.getBlockPos(), level, AddonHeaterInfos.BEAM_HEAT_CONVERTER);
        this.emitWorkingParticles(level);
    }

    private void rollCycleIndex(ServerLevel level) {
        this.cycleIndex = this.pickNextCycleIndex(level);
        level.updateNeighbourForOutputSignal(this.getBlockPos(), this.getBlockState().getBlock());
    }

    private int pickNextCycleIndex(ServerLevel level) {
        int next = level.random.nextInt(MAX_CYCLE_INDEX) + 1;
        if (next == this.cycleIndex) {
            next = next % MAX_CYCLE_INDEX + 1;
        }
        return next;
    }

    private boolean isLaserRequirementMet() {
        return this.cycleIndex > 0 && this.laserLevel == this.cycleIndex;
    }

    private void setWorking(boolean working, ServerLevel level) {
        if (this.working == working) {
            return;
        }
        this.working = working;
        this.setChanged();
        BlockState state = this.getBlockState();
        if (state.getValue(BeamHeatConverterBlock.WORKING) != working) {
            level.setBlockAndUpdate(this.getBlockPos(), state.setValue(BeamHeatConverterBlock.WORKING, working));
        }
    }

    private void emitWorkingParticles(ServerLevel level) {
        if (level.getGameTime() % 5 != 0) {
            return;
        }
        BlockPos pos = this.getBlockPos();
        level.sendParticles(
            ParticleTypes.FLAME,
            pos.getX() + 0.5,
            pos.getY() + 1.05,
            pos.getZ() + 0.5,
            2,
            1.1,
            0.1,
            1.1,
            0.01
        );
    }

    /**
     * 工作状态下需要加热的 3x3x3 区域。
     */
    public Set<BlockPos> getHeatingPoses() {
        if (!this.working) {
            return Set.of();
        }
        Set<BlockPos> poses = new HashSet<>(26);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    poses.add(this.getBlockPos().offset(dx, dy, dz));
                }
            }
        }
        return poses;
    }

    public int getLaserLevel() {
        return this.laserLevel;
    }

    /**
     * 比较器输出：以当前周期序号作为强度，供红石检测当前周期序号使用。
     */
    public int getComparatorOutput() {
        return this.cycleIndex;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("CycleIndex", this.cycleIndex);
        tag.putInt("CycleTicks", this.cycleTicks);
        tag.putInt("SatisfiedStreak", this.satisfiedStreak);
        tag.putBoolean("Working", this.working);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.cycleIndex = tag.getInt("CycleIndex");
        this.cycleTicks = tag.getInt("CycleTicks");
        this.satisfiedStreak = tag.getInt("SatisfiedStreak");
        this.working = tag.getBoolean("Working");
    }
}