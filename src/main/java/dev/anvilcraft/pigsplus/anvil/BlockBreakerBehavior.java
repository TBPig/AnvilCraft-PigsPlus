package dev.anvilcraft.pigsplus.anvil;

import dev.anvilcraft.pigsplus.block.BlockBreakerBlock;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import dev.dubhe.anvilcraft.block.BlockDevourerBlock;
import dev.dubhe.anvilcraft.block.BlockPlacerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBreakerBehavior implements IAnvilBehavior {
    @Override
    public boolean handle(Level level, BlockPos hitBlockPos, BlockState hitBlockState, float fallDistance, AnvilEvent.OnLand event) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        BlockBreakerBlock block = (BlockBreakerBlock) hitBlockState.getBlock();
        int distance = Math.min((int) event.getFallDistance() + 2, 5);
        level.setBlock(hitBlockPos, hitBlockState.setValue(BlockPlacerBlock.TRIGGERED, true), 2);
        block.breakBlock(
            serverLevel,
            hitBlockPos,
            hitBlockState.getValue(BlockDevourerBlock.FACING),
            distance,
            event.getEntity().getBlockState().getBlock()
        );
        level.scheduleTick(hitBlockPos, block, 4);
        return true;
    }
}
