package dev.anvilcraft.pigsplus.anvil;

import dev.anvilcraft.pigsplus.block.AutoChickenBlock;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AutoChickenBehavior implements IAnvilBehavior {
    @Override
    public boolean handle(Level level, BlockPos hitBlockPos, BlockState hitBlockState, float fallDistance, AnvilEvent.OnLand event) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        AutoChickenBlock block = (AutoChickenBlock) hitBlockState.getBlock();
        block.spawnEgg(serverLevel, hitBlockPos);
        return true;
    }
}
