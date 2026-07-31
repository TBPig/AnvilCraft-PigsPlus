package dev.anvilcraft.pigsplus.block;

import dev.anvilcraft.pigsplus.block.handler.BrassSinkFluidHandler;
import dev.dubhe.anvilcraft.api.fluid.FluidHandlerWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;

public class BrassSinkBlock extends Block {
    public BrassSinkBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if (FluidHandlerWrapper.tryInteractWithBottle(player, hand, BrassSinkFluidHandler.INSTANCE, level, pos)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection())) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
