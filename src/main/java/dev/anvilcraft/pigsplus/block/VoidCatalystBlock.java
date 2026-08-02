package dev.anvilcraft.pigsplus.block;

import dev.dubhe.anvilcraft.block.VoidMatterBlock;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class VoidCatalystBlock extends Block {

    public VoidCatalystBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        void_decay(state, level, pos, random);
    }

    public static void void_decay(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.relative(direction);
            BlockState blockState = level.getBlockState(targetPos);
            if (blockState.is(ModBlocks.VOID_MATTER_BLOCK)) {
                level.setBlockAndUpdate(targetPos, VoidMatterBlock.voidDecay(level, targetPos, state, random));
            }
        }
    }
}

