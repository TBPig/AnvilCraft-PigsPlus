package dev.anvilcraft.pigsplus.block;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Redstone;

public class RedstoneConduitBlock extends Block implements IHammerRemovable {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    private boolean shouldSignal = true;

    public RedstoneConduitBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        updatePower(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, neighborBlock, fromPos, moving);
        this.updatePower(level, pos);
    }

    private void updatePower(Level level, BlockPos pos) {
        if (level.isClientSide) return;

        // 计算期间临时关闭信号输出，防止自我反馈
        this.shouldSignal = false;

        int pow = 0;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            int signal;
            if (neighborState.is(AddonBlocks.REDSTONE_CONDUIT_BLOCK)) {
                signal = neighborState.getValue(POWER) - 1;
            } else if (neighborState.is(Blocks.REDSTONE_WIRE)) {
                signal = level.getSignal(neighborPos, direction) - 1;
            } else if (neighborState.is(ModBlocks.REDSTONE_WIRE)) {
                signal = level.getSignal(neighborPos, direction) - 1;
            } else {
                signal = level.getSignal(neighborPos, direction);
            }

            pow = Math.max(pow, signal);
            if (pow >= Redstone.SIGNAL_MAX) break;
        }

        this.shouldSignal = true;

        int clamped = Mth.clamp(pow, Redstone.SIGNAL_MIN, Redstone.SIGNAL_MAX);
        BlockState state = level.getBlockState(pos);
        if (state.getValue(POWER) != clamped) {
            level.setBlock(pos, state.setValue(POWER, clamped), Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return this.shouldSignal;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return !this.shouldSignal ? 0 : blockState.getValue(POWER);
    }
}
