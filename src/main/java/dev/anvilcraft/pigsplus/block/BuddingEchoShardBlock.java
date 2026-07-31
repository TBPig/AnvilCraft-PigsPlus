package dev.anvilcraft.pigsplus.block;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.api.block.IBrokenCrystalsBudding;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
public class BuddingEchoShardBlock extends Block implements IBrokenCrystalsBudding {

    public BuddingEchoShardBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        transform_block(level, pos, random);
        transform_amethyst(level, pos, random);
    }

    @Override
    public void anvilcraft$tryGrowBuds(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel level1)) return;

        RandomSource random = level.getRandom();
        transform_amethyst(level1, pos, random);
    }

    @Override
    public void anvilcraft$tryBreakClusters(Level level, BlockPos pos, BlockState state, BiConsumer<BlockPos, BlockState> breaker) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(AddonBlocks.ECHO_CLUSTER.get())) {
                breaker.accept(neighborPos, neighborState);
            }
        }
    }

    protected static void transform_block(ServerLevel level, BlockPos pos, RandomSource random) {
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
                        return true;
                    } else {
                        return state.is(Blocks.SCULK) || state.is(AddonBlocks.BUDDING_ECHO_SHARD);
                    }
                }
            ) >= 1
        ) {
            level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 1.1F);
        }

    }

    protected static void grow(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState state) {
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

    protected static void transform_amethyst(ServerLevel level, BlockPos pos, RandomSource random) {
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