package dev.anvilcraft.pigsplus.item;

import dev.dubhe.anvilcraft.init.block.ModFluidTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

import static net.minecraft.world.level.block.Block.dropResources;

public class MengerSpongeStaffItem extends Item {
    
    public MengerSpongeStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        removeFluidBreadthFirstSearch(level, pos);
        player.getCooldowns().addCooldown(this, 5);
        return InteractionResult.sidedSuccess(level.isClientSide());

    }

    // 与门格海绵方块一致
    static public void removeFluidBreadthFirstSearch(Level level, BlockPos pos) {
        BlockPos.breadthFirstTraversal(
            pos,
            6,
            65,
            (posx, consumer) -> {
                for (Direction direction : Direction.values()) {
                    consumer.accept(posx.relative(direction));
                }
            },
            (checkedPos) -> {
                if (checkedPos.equals(pos)) {
                    return true;
                }
                BlockState blockState = level.getBlockState(checkedPos);
                FluidState fluidState = level.getFluidState(checkedPos);
                if (!fluidState.is(ModFluidTags.MENGER_SPONGE_CAN_ABSORB)) {
                    return false;
                }
                Block block = blockState.getBlock();
                if (block instanceof BucketPickup bucketPickup) {
                    if (!bucketPickup
                        .pickupBlock(null, level, checkedPos, blockState)
                        .isEmpty()) {
                        return true;
                    }
                }

                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(checkedPos, Blocks.AIR.defaultBlockState(), 3);
                } else {
                    if (!blockState.is(Blocks.KELP)
                        && !blockState.is(Blocks.KELP_PLANT)
                        && !blockState.is(Blocks.SEAGRASS)
                        && !blockState.is(Blocks.TALL_SEAGRASS)) {
                        return false;
                    }

                    BlockEntity blockEntity =
                        blockState.hasBlockEntity() ? level.getBlockEntity(checkedPos) : null;
                    dropResources(blockState, level, checkedPos, blockEntity);
                    level.setBlock(checkedPos, Blocks.AIR.defaultBlockState(), 3);
                }
                return true;
            }
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.menger_sponge_staff"
            ).withStyle(ChatFormatting.GRAY));
    }
}

