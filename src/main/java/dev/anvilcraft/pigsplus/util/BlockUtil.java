package dev.anvilcraft.pigsplus.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BlockUtil {
    public static int countBlocks(Level level, BlockPos blockPos, List<BlockPos> positions, Block target) {
        int count = 0;
        for (BlockPos pos : positions) {
            BlockPos targetPos = blockPos.offset(pos);
            if (level.isOutsideBuildHeight(targetPos)) continue;
            if (level.getBlockState(targetPos).is(target)) count++;
        }
        return count;
    }
    public static int countBlocks(Level level, BlockPos blockPos, List<BlockPos> positions, Holder<Block> target) {
        return countBlocks(level, blockPos, positions, target.value());
    }

    public static int countBlocks(Level level, BlockPos blockPos, List<BlockPos> positions, TagKey<Block> targets) {
        int count = 0;
        for (BlockPos pos : positions) {
            BlockPos targetPos = blockPos.offset(pos);
            if (level.isOutsideBuildHeight(targetPos)) continue;
            if (level.getBlockState(targetPos).is(targets)) count++;
        }
        return count;
    }
}
