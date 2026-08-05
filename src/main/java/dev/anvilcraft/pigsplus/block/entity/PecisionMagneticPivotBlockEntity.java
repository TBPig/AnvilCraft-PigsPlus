package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.PecisionMagneticPivotBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.api.chargecollector.ChargeCollectorManager;
import dev.dubhe.anvilcraft.util.MagnetUtil;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PecisionMagneticPivotBlockEntity extends BlockEntity {
    @Getter
    private int time = 0;
    @Getter
    private int frictionCount = 0;
    @Getter
    private int clockwise = 0;

    private @Nullable Direction preDirection;

    public PecisionMagneticPivotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.time = tag.getInt("Time");
        this.frictionCount = tag.getInt("PushCount");
        this.clockwise = tag.getInt("Clockwise");
        String dir = tag.getString("PreDirection");
        this.preDirection = "null".equals(dir) ? null : Direction.byName(dir);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("Time", this.time);
        tag.putInt("PushCount", this.frictionCount);
        tag.putInt("Clockwise", this.clockwise);
        tag.putString("PreDirection", this.preDirection != null ? this.preDirection.getName() : "null");
    }

    public @Nullable Direction getPreDirection() {
        return this.preDirection;
    }

    public static void onPistonMoveBlocks(Level level, List<BlockPos> blocks, Direction pushDirection) {
        for (BlockPos pos : blocks) {
            if (!MagnetUtil.hasMagnetism(level, pos.below())) continue;
            for (int i = 0; i < 4; i++) {
                Direction dir = Direction.from2DDataValue(i);
                BlockPos neighborPos = pos.relative(dir);
                if (!level.getBlockState(neighborPos).is(AddonBlocks.PRECISION_MAGNETIC_PIVOT.get())) continue;
                if (level.getBlockEntity(neighborPos) instanceof PecisionMagneticPivotBlockEntity be) {
                    be.onAdjacentMagnetPushed(dir.getOpposite(), pushDirection);
                }
            }
        }
    }

    public void onAdjacentMagnetPushed(Direction sourceDirection, Direction pushDirection) {
        Level level = this.level;
        if (level == null || level.isClientSide()) return;
        if (!pushDirection.getAxis().isHorizontal()) return;
        if (!sourceDirection.getAxis().isHorizontal()) return;
        if (pushDirection.getAxis() == sourceDirection.getAxis()) return;

        this.friction(pushDirection);
        preDirection = pushDirection;
        setChanged();

        if (frictionCount == 4) {
            tryCharge(level);
            frictionCount = 0;
            time = getMaxTime();
            level.setBlockAndUpdate(
                this.getBlockPos(),
                AddonBlocks.PRECISION_MAGNETIC_PIVOT.getDefaultState().setValue(PecisionMagneticPivotBlock.LIT, true)
            );
        }
    }

    private void tryCharge(Level level) {
        int power = AnvilCraftPigsPlus.CONFIG.precisionMagneticPivotPower;
        int maxTime = getMaxTime();
        int time = maxTime - this.time;
        if (!this.isWorking()) {
            ChargeCollectorManager.charge(power, level, this.getBlockPos());
        } else {
            int p = Math.round(power * Mth.sqrt((float) time / maxTime) * Mth.sqrt((float) (time + 2 * maxTime) / maxTime / 3));
            ChargeCollectorManager.charge(p, level, this.getBlockPos());
        }
    }

    private void friction(Direction pushDirection) {
        if (preDirection == null) {
            frictionCount = 1;

        } else if (pushDirection == preDirection.getClockWise()) {
            if (clockwise != -1) {
                frictionCount++;
            } else {
                frictionCount = 1;
            }
            clockwise = 1;

        } else if (pushDirection == preDirection.getCounterClockWise()) {
            if (clockwise != 1) {
                frictionCount++;
            } else {
                frictionCount = 1;
            }
            clockwise = -1;

        } else if (pushDirection != preDirection) {
            frictionCount = 1;
            clockwise = 0;
        }
    }

    public void tick(ServerLevel serverLevel, BlockPos pos) {
        if (this.time > 0) {
            this.time--;
            if (this.time == 0 && serverLevel.getBlockState(pos).is(AddonBlocks.PRECISION_MAGNETIC_PIVOT)) {
                serverLevel.setBlockAndUpdate(
                    pos,
                    AddonBlocks.PRECISION_MAGNETIC_PIVOT.getDefaultState().setValue(PecisionMagneticPivotBlock.LIT, false)
                );
            }
        }
    }

    public boolean isWorking() {
        return isWorking(this.time);
    }

    public static boolean isWorking(int time) {
        return time > 0;
    }

    public static int getMaxTime() {
        return AnvilCraftPigsPlus.CONFIG.precisionMagneticPivotMaxTime;
    }
}
