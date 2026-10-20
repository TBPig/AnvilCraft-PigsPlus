package dev.anvilcraft.pigsplus.block;

import dev.anvilcraft.pigsplus.util.BlockBreakerUtil;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import dev.dubhe.anvilcraft.block.BlockDevourerBlock;
import dev.dubhe.anvilcraft.block.BlockPlacerBlock;
import dev.dubhe.anvilcraft.util.AnvilUtil;
import dev.dubhe.anvilcraft.util.BlockMiningEffect;
import dev.dubhe.anvilcraft.util.DevourUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockBreakerBlock extends BlockDevourerBlock {
    public static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 8, 16, 16, 16);
    public static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 0, 16, 16, 8);
    public static final VoxelShape WEST_SHAPE = Block.box(8, 0, 0, 16, 16, 16);
    public static final VoxelShape EAST_SHAPE = Block.box(0, 0, 0, 8, 16, 16);
    public static final VoxelShape UP_SHAPE = Block.box(0, 0, 0, 16, 8, 16);
    public static final VoxelShape DOWN_SHAPE = Block.box(0, 8, 0, 16, 16, 16);

    @Override
    public VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    public BlockBreakerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void devourBlock(ServerLevel level, BlockPos devourerPos, Direction devourerDirection, int range) {
    }

    @Override
    protected void onPlace(
        BlockState state,
        Level level,
        BlockPos pos,
        BlockState oldState,
        boolean movedByPiston
    ) {
        if (!level.isClientSide) {
            checkIfTriggered(level, state, pos);
        }
    }

    @Override
    public void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block neighborBlock,
        BlockPos neighborPos,
        boolean movedByPiston
    ) {
        if (!level.isClientSide) {
            checkIfTriggered(level, state, pos);
        }
    }

    private void checkIfTriggered(Level level, BlockState blockState, BlockPos blockPos) {
        boolean bl = blockState.getValue(TRIGGERED);
        if (bl != BlockPlacerBlock.hasNeighborSignal(level, blockPos, blockState.getValue(FACING))) {
            BlockState changedState = blockState.setValue(TRIGGERED, !bl);
            level.setBlock(blockPos, changedState, 2);
            if (!bl) {
                breakBlock((ServerLevel) level, blockPos, blockState.getValue(FACING), 1, null);
            }
        }
    }

    public void breakBlock(
        ServerLevel level,
        BlockPos breakerPos,
        Direction breakerDirection,
        int distance,
        @Nullable Block anvil
    ) {
        BlockPos outputPos = breakerPos.relative(breakerDirection.getOpposite());

        // 因为实现方式不一样，DistanceMax - 1 才和放置器相同
        int i = 0;
        do {
            if (
                level.getBlockState(outputPos).is(this)
                && level.getBlockState(outputPos).getValue(BlockDevourerBlock.FACING).equals(breakerDirection)
            ) {
                i++;
                outputPos = outputPos.relative(breakerDirection.getOpposite());
            } else {
                break;
            }
        } while (i < AnvilCraft.CONFIG.world.blockPlacerMaxRecursiveRetrievalDistance - 1);

        final List<IItemHandler> outputItemHandlerList = ItemHandlerUtil.getTargetItemHandlerList(outputPos, breakerDirection, level);

        BlockPos targetPos = breakerPos.relative(breakerDirection, distance);
        if (level.isOutsideBuildHeight(targetPos)) return;

        BlockMiningEffect miningEffect = BlockMiningEffect.fromAnvil(anvil).orElse(BlockMiningEffect.NORMAL);

        List<BlockPos> breakBlockPosList = DevourUtil.getDevourPosList(level, targetPos, breakerDirection, 0, 0);
        for (BlockPos breakBlockPos : breakBlockPosList) {
            BlockBreakerUtil.BreakResult result = BlockBreakerUtil.breakBlock(
                level,
                breakBlockPos,
                miningEffect,
                null,
                AnvilCraft.CONFIG.world.blockDevourerProtectContainers,
                true
            );
            if (!result.destroyed()) continue;
            this.distributeDrops(level, breakBlockPos, result.drops(), outputItemHandlerList, outputPos);
        }
    }

    private void distributeDrops(
        ServerLevel level,
        BlockPos breakBlockPos,
        List<ItemStack> drops,
        @Nullable List<IItemHandler> itemHandlerList,
        BlockPos outputPos
    ) {
        Vec3 center = outputPos.getCenter();
        AABB aabb = new AABB(center.add(-0.125, -0.125, -0.125), center.add(0.125, 0.125, 0.125));
        final boolean insertEnabled = itemHandlerList != null && !itemHandlerList.isEmpty();
        final boolean dropOriginalPlace = !level.noCollision(aabb);

        for (ItemStack itemStack : drops) {
            if (insertEnabled) {
                for (IItemHandler target : itemHandlerList) {
                    itemStack = ItemHandlerHelper.insertItemStacked(target, itemStack, false);
                }
            }
            if (itemStack.isEmpty()) continue;
            if (dropOriginalPlace) {
                Block.popResource(level, breakBlockPos, itemStack);
            } else {
                AnvilUtil.dropItems(List.of(itemStack), level, center);
            }
        }
    }
}
