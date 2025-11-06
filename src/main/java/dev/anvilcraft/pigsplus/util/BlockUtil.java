package dev.anvilcraft.pigsplus.util;

import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BlockUtil {
    public static int countBlocks(Level level, BlockPos blockPos, int distance, Block target) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                for (int k = -distance; k <= distance; k++) {
                    mpos.set(blockPos).move(i, j, k);
                    if (level.isOutsideBuildHeight(mpos)) continue;
                    if (level.getBlockState(mpos).is(target)) count++;
                }
            }
        }
        return count;
    }
    public static int countBlocks(Level level, BlockPos blockPos, int distance, Holder<Block> target) {
        return countBlocks(level, blockPos, distance, target.value());
    }
}
