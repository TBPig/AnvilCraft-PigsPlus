package dev.anvilcraft.pigsplus.block;

import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public class RedstoneConduitBlock extends Block implements IHammerRemovable {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

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
        updatePower(state, level, pos);
    }

    @Override
    protected void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block block,
        @Nullable Orientation orientation,
        boolean movedByPiston
    ) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        this.updatePower(state, level, pos);
    }

    public void updatePower(BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            int pow = level.getBestNeighborSignal(pos);
            level.setBlock(pos, state.setValue(POWER, Mth.clamp(pow, 0, 15)), 1 | 2 | 4);
        }
    }
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return Math.max(0, blockState.getValue(POWER) - 1);
    }
}