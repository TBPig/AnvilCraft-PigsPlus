package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.CauldronOutputBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CauldronOutputBlockEntity extends BlockEntity {
    public CauldronOutputBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void tick() {
        if (level == null) return;
        if (level.hasNeighborSignal(getBlockPos())) return;

        BlockState state = getBlockState();
        if (!state.is(AddonBlocks.CAULDRON_OUTPUT.get())) return;

        Direction facing = state.getValue(CauldronOutputBlock.FACING);
        BlockPos cauldronPos = getBlockPos().relative(facing.getOpposite());
        BlockState cauldronState = level.getBlockState(cauldronPos);
        if (cauldronState.is(BlockTags.CAULDRONS)) {
            extractItemFromCauldron(cauldronPos, facing, level);
            extractFluidFromCauldron(cauldronPos, level);
        } else {
            IItemHandler source = ItemHandlerUtil.getSourceItemHandler(cauldronPos, facing, level);
            if (source == null) return;

            extractItemFromSource(facing, level, source);
        }
    }

    private void extractItemFromCauldron(BlockPos cauldronPos, Direction facing, Level level) {
        AABB searchBox = new AABB(cauldronPos);
        level.getEntities(EntityType.ITEM, searchBox, (e) -> !e.anvilcraft$isAdsorbable())
            .forEach(entity -> {
                Vec3 targetPos = getBlockPos().getCenter().add(
                    -facing.getStepX() * 0.2,
                    -0.3,
                    -facing.getStepZ() * 0.2
                );
                entity.teleportTo(targetPos.x, targetPos.y, targetPos.z);


                entity.setDeltaMovement(Vec3.ZERO);
            });
    }

    private void extractFluidFromCauldron(BlockPos cauldronPos, Level level) {
        BlockPos outputPos = getBlockPos().below();
        BlockState inputState = level.getBlockState(cauldronPos);
        BlockState outputState = level.getBlockState(outputPos);

        if (outputState.is(Blocks.CAULDRON) &&
            inputState.getBlock() instanceof AbstractCauldronBlock cauldron &&
            cauldron.isFull(inputState)) {
            level.setBlock(outputPos, inputState, Block.UPDATE_ALL);
            level.setBlock(cauldronPos, outputState, Block.UPDATE_ALL);
        }
    }

    private void extractItemFromSource(Direction facing, Level level, IItemHandler source) {
        ItemStackHandler itemHandler = new ItemStackHandler();
        ItemHandlerUtil.importFromTarget(itemHandler, 64, stack -> true, source);
        // 将itemHandler的物品输出到世界中
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            Vec3 targetPos = getBlockPos().getCenter()
                .add(-facing.getStepX() * 0.2, -0.3, -facing.getStepZ() * 0.2);
            ItemEntity itemEntity = new ItemEntity(level, targetPos.x, targetPos.y, targetPos.z, stack.copy(), 0, 0, 0);
            level.addFreshEntity(itemEntity);
        }
    }
}