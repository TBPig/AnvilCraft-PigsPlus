package dev.anvilcraft.pigsplus.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.api.itemhandler.FilteredItemStackHandler;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ElectricEnchantingTableBlock extends BaseEntityBlock implements IHammerRemovable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OVERLOAD = IPowerComponent.OVERLOAD;

    public ElectricEnchantingTableBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition()
            .any()
            .setValue(POWERED, false)
            .setValue(OVERLOAD, true));
    }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ElectricEnchantingTableBlock::new);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(POWERED, false).setValue(OVERLOAD, true);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(
            type,
            AddonBlockEntities.ELECTRIC_ENCHANTING_TABLE.get(),
            (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos)
        );
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
        if (level.isClientSide) {
            return;
        }
        level.setBlock(pos, state.setValue(POWERED, level.hasNeighborSignal(pos)), 2);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ElectricEnchantingTableBlockEntity(AddonBlockEntities.ELECTRIC_ENCHANTING_TABLE.get(), blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED).add(OVERLOAD);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(
        BlockState state,
        Level level,
        BlockPos pos,
        BlockState newState,
        boolean movedByPiston
    ) {
        if (state.is(newState.getBlock())) return;
        if (level.getBlockEntity(pos) instanceof ElectricEnchantingTableBlockEntity entity) {
            Vec3 vec3 = entity.getBlockPos().getCenter();
            FilteredItemStackHandler depository = entity.getFilteredItemStackHandler();
            for (int slot = 0; slot < depository.getSlots(); slot++) {
                Containers.dropItemStack(level, vec3.x, vec3.y, vec3.z, depository.getStackInSlot(slot));
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(POWERED), 2);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof ElectricEnchantingTableBlockEntity table ? table.getAnalogRedstoneSignal() : 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        if (level.getBlockEntity(pos) instanceof ElectricEnchantingTableBlockEntity table) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            // 玩家空手时尝试取出物品
            if (stack.isEmpty()) {
                ItemStack itemInSlot = table.getFilteredItemStackHandler().getStackInSlot(2);
                if (!itemInSlot.isEmpty()) {
                    ItemStack extracted = table.getFilteredItemStackHandler().extractItem(2, itemInSlot.getCount(), false);
                    player.getInventory().placeItemBackInInventory(extracted);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f + level.getRandom().nextFloat());
                    return ItemInteractionResult.SUCCESS;
                }

            } else {
                ItemStack result = table.getFilteredItemStackHandler().insertItem(0, stack, true);
                if (result.isEmpty() || result.getCount() < stack.getCount()) {
                    int countDiff = stack.getCount() - (result.isEmpty() ? 0 : result.getCount());
                    ItemStack toInsert = stack.split(countDiff);
                    table.getFilteredItemStackHandler().insertItem(0, toInsert, false);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
