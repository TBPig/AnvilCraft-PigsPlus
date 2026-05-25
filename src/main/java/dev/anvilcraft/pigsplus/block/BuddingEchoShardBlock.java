package dev.anvilcraft.pigsplus.block;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BuddingEchoShardBlock extends Block {
    public static final int GROWTH_CHANCE = 5;

    public BuddingEchoShardBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        transform_block(level, pos, random);
        if (random.nextInt(GROWTH_CHANCE) == 0) {
            transform_amethyst(level, pos, random);
        }
    }

    private static void transform_block(ServerLevel level, BlockPos pos, RandomSource random) {
        // 将距离5以内的所有可被幽匿转化的方块变为幽匿块
        if (
            BlockPos.breadthFirstTraversal(
                pos, 4, (int) Math.pow(2, 7),
                (blockPos, consumer) -> {
                    for (Direction direction : Direction.values()) {
                        consumer.accept(blockPos.relative(direction));
                    }
                },
                blockPos -> {
                    BlockState state = level.getBlockState(blockPos);
                    if (state.is(BlockTags.SCULK_REPLACEABLE)) {
                        grow(level, random, blockPos, state);
                        return BlockPos.TraversalNodeStatus.ACCEPT;
                    } else if (state.is(Blocks.SCULK)) {
                        return BlockPos.TraversalNodeStatus.ACCEPT;
                    } else if (state.is(AddonBlocks.BUDDING_ECHO_SHARD)) {
                        return BlockPos.TraversalNodeStatus.ACCEPT;
                    } else {
                        return BlockPos.TraversalNodeStatus.SKIP;
                    }
                }
            ) >= 1
        ) {
            level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 1.1F);
        }

    }

    private static void grow(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState state) {
        if (!state.is(Blocks.SCULK)) {
            level.setBlockAndUpdate(blockPos, Blocks.SCULK.defaultBlockState());
        }
        if (level.getBlockState(blockPos.above()).isAir()) {
            int rand = random.nextInt(100);
            if (rand < 4) {
                level.setBlockAndUpdate(blockPos.above(), Blocks.SCULK_SENSOR.defaultBlockState());
            } else if (rand < 5) {
                level.setBlockAndUpdate(blockPos.above(), Blocks.SCULK_SHRIEKER.defaultBlockState());
            } else if (rand < 6) {
                level.setBlockAndUpdate(blockPos.above(), Blocks.SCULK_CATALYST.defaultBlockState());
            }
        }
    }

    private static void transform_amethyst(ServerLevel level, BlockPos pos, RandomSource random) {
        Direction direction = Direction.getRandom(random);
        BlockPos blockPos = pos.relative(direction);
        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.is(Blocks.AMETHYST_CLUSTER) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
            level.setBlockAndUpdate(
                blockPos, AddonBlocks.ECHO_CLUSTER.get().defaultBlockState()
                    .setValue(EchoClusterBlock.FACING, direction)
                    .setValue(EchoClusterBlock.WATERLOGGED, false)
            );
        }
    }
}