package dev.anvilcraft.pigsplus.anvil;

import dev.anvilcraft.pigsplus.block.BlockBreakerBlock;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import dev.dubhe.anvilcraft.block.utility.BlockDevourerBlock;
import dev.dubhe.anvilcraft.block.utility.BlockPlacerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBreakerBehavior implements IAnvilBehavior {
    @Override
    public boolean handle(ServerLevel level, BlockPos hitBlockPos, BlockState hitBlockState, double fallDistance, AnvilEvent.OnLand event) {
        BlockBreakerBlock block = (BlockBreakerBlock) hitBlockState.getBlock();
        int distance = Math.min((int) event.getFallDistance() + 2, 5);
        level.setBlock(hitBlockPos, hitBlockState.setValue(BlockPlacerBlock.TRIGGERED, true), 2);
        block.devourBlock(
            level,
            hitBlockPos,
            hitBlockState.getValue(BlockDevourerBlock.FACING),
            distance,
            event.getEntity().getBlockState().getBlock()
        );
        level.scheduleTick(hitBlockPos, block, 4);
        return true;
    }
}
