package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class MengerSpongeHolyStaffItem extends MengerSpongeStaffItem {
    public MengerSpongeHolyStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    protected void afterFluidRemoval(UseOnContext context, boolean useOnCauldron) {
        if (!useOnCauldron) {
            spawnFakeMengerSponge(context);
        }
    }

    @Override
    protected int getCooldownTicks() {
        return 2;
    }

    public static void spawnFakeMengerSponge(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockPos placePos = placeContext.getClickedPos();
        BlockState existingState = level.getBlockState(placePos);
        BlockState state = AddonBlocks.FAKE_MENGER_SPONGE.get().defaultBlockState();

        if (
            existingState.canBeReplaced()
            && level.isUnobstructed(state, placePos, collisioncontext)
            && level.getWorldBorder().isWithinBounds(placePos)
        ) {
            level.setBlock(placePos, state, Block.UPDATE_ALL_IMMEDIATE);
        }
    }
}
