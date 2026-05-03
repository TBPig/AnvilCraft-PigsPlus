package dev.anvilcraft.pigsplus.anvil;

import dev.anvilcraft.pigsplus.block.VoidCatalystBlock;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class VoidCatalystBehavior implements IAnvilBehavior {
    @Override
    public boolean handle(Level level, BlockPos hitBlockPos, BlockState hitBlockState, float fallDistance, AnvilEvent.OnLand event) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        VoidCatalystBlock.void_decay(hitBlockState, serverLevel, hitBlockPos, serverLevel.random);
        return true;
    }
}
