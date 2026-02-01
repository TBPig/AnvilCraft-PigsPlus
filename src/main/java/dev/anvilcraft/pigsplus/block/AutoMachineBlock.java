package dev.anvilcraft.pigsplus.block;

import dev.anvilcraft.pigsplus.block.entity.AutoMachineBlockEntity;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.block.better.BetterBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class AutoMachineBlock extends BetterBaseEntityBlock implements IHammerRemovable {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OVERLOAD = IPowerComponent.OVERLOAD;

    protected AutoMachineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition
            .any()
            .setValue(POWERED, false)
            .setValue(OVERLOAD, true)
            .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED).add(OVERLOAD).add(FACING);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getNearestLookingDirection().getOpposite();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            dir = dir.getOpposite();
        }
        return this.defaultBlockState()
            .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
            .setValue(OVERLOAD, true)
            .setValue(FACING, dir);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof AutoMachineBlockEntity autoMachineBlockEntity) {
            return autoMachineBlockEntity.getRedstoneSignal();
        }
        return 0;
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
    public void tick(
        BlockState state,
        ServerLevel level,
        BlockPos pos,
        RandomSource random
    ) {
        if (state.getValue(POWERED) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(POWERED), 2);
        }
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
        if (level.getBlockEntity(pos) instanceof AutoMachineBlockEntity entity) {
            Vec3 vec3 = entity.getBlockPos().getCenter();
            IItemHandler itemHandler = entity.getItemHandler();
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                Containers.dropItemStack(level, vec3.x, vec3.y, vec3.z, itemHandler.getStackInSlot(slot));
            }
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

}