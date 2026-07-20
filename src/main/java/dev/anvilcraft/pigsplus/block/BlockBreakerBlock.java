package dev.anvilcraft.pigsplus.block;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.entity.fakeplayer.AnvilCraftFakePlayers;
import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import dev.dubhe.anvilcraft.block.BlockDevourerBlock;
import dev.dubhe.anvilcraft.util.AnvilUtil;
import dev.dubhe.anvilcraft.util.BlockMiningEffect;
import dev.dubhe.anvilcraft.util.BreakBlockUtil;
import dev.dubhe.anvilcraft.util.DevourUtil;
import dev.dubhe.anvilcraft.util.PistonMoveGuard;
import dev.dubhe.anvilcraft.util.TriggerUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
        } while (i < AnvilCraft.CONFIG.blockPlacerRecursiveRetrievalDistanceMax - 1);

        final List<IItemHandler> outputItemHandlerList = ItemHandlerUtil.getTargetItemHandlerList(outputPos, breakerDirection, level);

        BlockPos breakBlockPos = breakerPos.relative(breakerDirection, distance);
        if (level.isOutsideBuildHeight(breakBlockPos)) return;

        BlockMiningEffect miningEffect = BlockMiningEffect.fromAnvil(anvil).orElse(BlockMiningEffect.NORMAL);

        devourSingleBlockInternalLogic(level, miningEffect, breakBlockPos, outputItemHandlerList, outputPos);
    }

    private static void devourSingleBlockInternalLogic(
        ServerLevel level,
        BlockMiningEffect miningEffect,
        BlockPos breakBlockPos,
        @Nullable List<IItemHandler> itemHandlerList,
        BlockPos outputPos
    ) {
        if (PistonMoveGuard.isReserved(level, breakBlockPos)) return;

        Vec3 center = outputPos.getCenter();
        AABB aabb = new AABB(center.add(-0.125, -0.125, -0.125), center.add(0.125, 0.125, 0.125));

        BlockState breakBlockState = level.getBlockState(breakBlockPos);
        if (!DevourUtil.shouldDevour(breakBlockState)) return;

        List<ItemStack> dropList = BreakBlockUtil.drop(level, breakBlockPos, miningEffect);
        final boolean insertEnabled = itemHandlerList != null && !itemHandlerList.isEmpty();
        final boolean dropOriginalPlace = !level.noCollision(aabb);

        if (miningEffect.isDisintegration()) {
            BreakBlockUtil.dropExperience(level, breakBlockPos, breakBlockState, miningEffect);
        } else {
            IItemHandler source = level.getCapability(Capabilities.ItemHandler.BLOCK, breakBlockPos, null);
            boolean skipContentTransfer = source == null;
            for (ItemStack itemStack : dropList) {
                skipContentTransfer |= ItemHandlerUtil.isEmptyContainer(itemStack);
                if (insertEnabled) {
                    for (IItemHandler target : itemHandlerList) {
                        itemStack = ItemHandlerHelper.insertItemStacked(target, itemStack, false);
                    }
                }
                if (itemStack.isEmpty() && ItemHandlerUtil.isEmptyContainer(source)) continue;
                if (dropOriginalPlace) {
                    Block.popResource(level, breakBlockPos, itemStack);
                } else {
                    AnvilUtil.dropItems(List.of(itemStack), level, center);
                }
            }
            if (!skipContentTransfer) {
                if (insertEnabled) ItemHandlerUtil.exportContentsToItemHandlers(source, itemHandlerList);
                if (!dropOriginalPlace) ItemHandlerUtil.dropAllToPos(source, level, center);
            }
        }
        if (level.getBlockEntity(breakBlockPos) instanceof LecternBlockEntity lectern) {
            transferLecternContents(level, itemHandlerList, center, lectern, insertEnabled, dropOriginalPlace);
        }
        if (!(breakBlockState.getBlock() instanceof DoublePlantBlock)) {
            breakBlockState.getBlock().playerWillDestroy(
                level,
                breakBlockPos,
                breakBlockState,
                AnvilCraftFakePlayers.anvilcraftBlockPlacer.getPlayer()
            );
        }
        level.destroyBlock(breakBlockPos, false);
        TriggerUtil.devourerDevourBlock(level, breakBlockPos, breakBlockState.getBlock());
    }

    /**
     * 转移讲台内容
     *
     * <p>虽然溜槽/漏斗无法与讲台交互，但吞噬器这类直接破坏的应该转移走才正常点</p>
     */
    private static void transferLecternContents(
        ServerLevel level,
        @Nullable List<IItemHandler> itemHandlerList,
        Vec3 center,
        LecternBlockEntity lectern,
        boolean insertEnabled,
        boolean dropOriginalPlace
    ) {
        ItemStack bookStack = lectern.getBook();
        if (insertEnabled) {
            assert itemHandlerList != null;
            for (IItemHandler target : itemHandlerList) {
                bookStack = ItemHandlerHelper.insertItem(target, bookStack, false);
                lectern.setBook(bookStack);
            }
        }
        if (!dropOriginalPlace) {
            AnvilUtil.dropItems(List.of(bookStack), level, center);
            lectern.setBook(ItemStack.EMPTY);
        }
    }
}