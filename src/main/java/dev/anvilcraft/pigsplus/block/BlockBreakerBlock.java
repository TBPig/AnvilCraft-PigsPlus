package dev.anvilcraft.pigsplus.block;

import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import dev.dubhe.anvilcraft.block.utility.BlockDevourerBlock;
import dev.dubhe.anvilcraft.block.workstation.TranscendenceAnvilBlock;
import dev.dubhe.anvilcraft.block.workstation.ember.EmberAnvilBlock;
import dev.dubhe.anvilcraft.block.workstation.royal.RoyalAnvilBlock;
import dev.dubhe.anvilcraft.util.AnvilUtil;
import dev.dubhe.anvilcraft.util.BreakBlockUtil;
import dev.dubhe.anvilcraft.util.TriggerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.dubhe.anvilcraft.api.entity.fakeplayer.AnvilCraftFakePlayers.anvilcraftBlockPlacer;
import static dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil.dropAllToPos;
import static dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil.exportContentsToItemHandlers;
import static dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil.getTargetItemHandlerList;
import static dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil.isEmptyContainer;
import static dev.dubhe.anvilcraft.util.MultiPartBlockUtil.getChainableMainPartPos;

public class BlockBreakerBlock extends BlockDevourerBlock {
    /**
     * @param properties 方块属性
     */
    public BlockBreakerBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings(
        {
            "unreachable",
            "unused"
        }
    )
    public void devourBlock(
        ServerLevel level,
        BlockPos breakerPos,
        Direction breakerDirection,
        int distance,
        @Nullable Block anvil
    ) {
        BlockPos breakBlockPos = breakerPos.relative(breakerDirection, distance);
        if (level.isOutsideBuildHeight(breakBlockPos)) return;

        BlockState breakerBlockState = level.getBlockState(breakBlockPos);
        if (breakerBlockState.isAir()) return;
        if (breakerBlockState.getBlock().defaultDestroyTime() < 0) return;

        breakBlockPos = getChainableMainPartPos(level, breakBlockPos);
        breakerBlockState = level.getBlockState(breakBlockPos);

        // 后面这些基本都是为了破坏方块以及正确存储容器内掉落物，ctrl+c/v过来的，我也看不懂就是了
        ResourceHandler<ItemResource> source = level.getCapability(Capabilities.Item.BLOCK, breakBlockPos, null);
        BlockPos outputPos = breakerPos.relative(breakerDirection.getOpposite());
        Vec3 center = outputPos.getCenter();
        AABB aabb = new AABB(center.add(-0.125, -0.125, -0.125), center.add(0.125, 0.125, 0.125));
        boolean dropOriginalPlace = !level.noCollision(aabb);
        boolean skipContentTransfer = source == null;
        final List<ResourceHandler<ItemResource>> itemHandlerList = getTargetItemHandlerList(outputPos, breakerDirection, level);
        boolean insertEnabled = !itemHandlerList.isEmpty();
        List<ItemStack> dropList = switch (anvil) {
            case RoyalAnvilBlock ignore -> BreakBlockUtil.dropSilkTouch(level, breakBlockPos);
            case EmberAnvilBlock ignore -> BreakBlockUtil.dropSmelt(level, breakBlockPos);
            case TranscendenceAnvilBlock ignore -> BreakBlockUtil.dropFortune5(level, breakBlockPos);
            case null, default -> BreakBlockUtil.drop(level, breakBlockPos);
        };
        for (ItemStack itemStack : dropList) {
            skipContentTransfer |= ItemHandlerUtil.isEmptyContainer(itemStack);
            if (insertEnabled) {
                for (ResourceHandler<ItemResource> target : itemHandlerList) {
                    itemStack = ResourceHandlerUtil.insertItemStacked(target, itemStack, false);
                }
            }
            if (itemStack.isEmpty() && isEmptyContainer(source)) continue;
            if (dropOriginalPlace) {
                Block.popResource(level, breakBlockPos, itemStack);
            } else {
                AnvilUtil.dropItems(List.of(itemStack), level, center);
            }
        }
        if (!skipContentTransfer) {
            if (insertEnabled) exportContentsToItemHandlers(source, itemHandlerList);
            if (!dropOriginalPlace) dropAllToPos(source, level, center);
        }
        if (level.getBlockEntity(breakBlockPos) instanceof LecternBlockEntity lectern) {
            transferLecternContents(level, itemHandlerList, center, lectern, insertEnabled, dropOriginalPlace);
        }
        if (!(breakerBlockState.getBlock() instanceof DoublePlantBlock)) {
            breakerBlockState.getBlock().playerWillDestroy(level, breakBlockPos, breakerBlockState, anvilcraftBlockPlacer.getPlayer());
        }

        level.destroyBlock(breakBlockPos, false);
        TriggerUtil.devourerDevourBlock(level, breakBlockPos, breakerBlockState.getBlock());
    }

    /**
     * 特判讲台的转移
     * 虽然溜槽/漏斗无法与讲台交互
     * 但吞噬器这类直接破坏的
     * 应该正常转移走才正常点(?)
     */
    private static void transferLecternContents(
        ServerLevel level,
        @Nullable List<ResourceHandler<ItemResource>> itemHandlerList,
        Vec3 center,
        LecternBlockEntity lectern,
        boolean insertEnabled,
        boolean dropOriginalPlace
    ) {
        ItemStack bookStack = lectern.getBook();
        if (insertEnabled) {
            assert itemHandlerList != null;
            for (ResourceHandler<ItemResource> target : itemHandlerList) {
                bookStack = ResourceHandlerUtil.insertItem(target, bookStack, false);
                lectern.setBook(bookStack);
            }
        }
        if (!dropOriginalPlace) {
            AnvilUtil.dropItems(List.of(bookStack), level, center);
            lectern.setBook(ItemStack.EMPTY);
        }
    }
}