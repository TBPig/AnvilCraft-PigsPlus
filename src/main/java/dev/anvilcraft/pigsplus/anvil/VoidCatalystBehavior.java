package dev.anvilcraft.pigsplus.anvil;

import dev.anvilcraft.pigsplus.block.VoidCatalystBlock;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class VoidCatalystBehavior implements IAnvilBehavior {
    @Override
    public boolean handle(ServerLevel level, BlockPos hitBlockPos, BlockState hitBlockState, double fallDistance, AnvilEvent.OnLand event) {
        VoidCatalystBlock.void_decay(hitBlockState, level, hitBlockPos, level.getRandom());
        return true;
    }
}
