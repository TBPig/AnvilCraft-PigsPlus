package dev.anvilcraft.pigsplus.item;

import dev.dubhe.anvilcraft.block.FishTankBlock;
import dev.dubhe.anvilcraft.block.ObsidianCauldron;
import dev.dubhe.anvilcraft.block.entity.FishTankBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

import static net.minecraft.world.level.block.Block.dropResources;

public class MengerSpongeStaffItem extends Item {

    public MengerSpongeStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (player.isShiftKeyDown()) return super.useOn(context);

        return this.useStaff(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) return InteractionResultHolder.pass(stack);

        BlockHitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(stack);

        InteractionResult result = this.useStaff(new UseOnContext(level, player, hand, stack, hitResult));
        return new InteractionResultHolder<>(result, stack);
    }

    protected InteractionResult useStaff(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        boolean useOnCauldron = removeFluidInCauldron(level, pos);
        if (!useOnCauldron) {
            removeFluidBreadthFirstSearch(level, pos);
        }
        this.afterFluidRemoval(context, useOnCauldron);

        player.getCooldowns().addCooldown(this, this.getCooldownTicks());
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    protected void afterFluidRemoval(UseOnContext context, boolean useOnCauldron) {
    }

    protected int getCooldownTicks() {
        return 4;
    }

    public static boolean removeFluidInCauldron(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof AbstractCauldronBlock abstractCauldronBlock
            && !(abstractCauldronBlock instanceof ObsidianCauldron)) {
            level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            return true;
        }
        if (state.getBlock() instanceof FishTankBlock) {
            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof FishTankBlockEntity fishTank)) return false;

            fishTank.getFluidHandler().drain(1000, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    // 与门格海绵方块一致
    public static void removeFluidBreadthFirstSearch(Level level, BlockPos pos) {
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
                BlockState blockState = level.getBlockState(checkedPos);
                FluidState fluidState = level.getFluidState(checkedPos);
                if (fluidState.isEmpty()) {
                    return checkedPos.equals(pos);
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
                    level.setBlock(checkedPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
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
                    level.setBlock(checkedPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
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

