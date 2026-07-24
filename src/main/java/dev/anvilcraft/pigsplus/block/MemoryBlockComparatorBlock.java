package dev.anvilcraft.pigsplus.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.lib.v2.piston.IMoveableEntityBlock;
import dev.anvilcraft.lib.v2.util.ShapeUtil;
import dev.anvilcraft.lib.v2.util.Util;
import dev.anvilcraft.pigsplus.block.entity.MemoryBlockComparatorBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.anvilcraft.pigsplus.util.StructureDiskUtil;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.block.better.BetterBaseEntityBlock;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.util.StructureLoadUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MemoryBlockComparatorBlock extends BetterBaseEntityBlock implements IHammerRemovable, IMoveableEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty PRECISE = BooleanProperty.create("precise");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final VoxelShape BASE_SHAPE = Shapes.or(
        Block.box(2, 0, 2, 14, 4, 14),
        Block.box(5, 4, 5, 11, 10, 11),
        Block.box(4, 10, 4, 12, 16, 12)
    );

    private static final VoxelShape SHAPE_DOWN = BASE_SHAPE;
    private static final VoxelShape SHAPE_UP = ShapeUtil.rotate(Direction.Axis.X, 180, BASE_SHAPE);
    private static final VoxelShape SHAPE_SOUTH = ShapeUtil.rotate(Direction.Axis.X, 90, BASE_SHAPE);
    private static final VoxelShape SHAPE_NORTH = ShapeUtil.rotate(Direction.Axis.X, 270, BASE_SHAPE);
    private static final VoxelShape SHAPE_EAST = ShapeUtil.rotate(Direction.Axis.Y, 270, SHAPE_NORTH);
    private static final VoxelShape SHAPE_WEST = ShapeUtil.rotate(Direction.Axis.Y, 90, SHAPE_NORTH);

    public MemoryBlockComparatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition
                .any()
                .setValue(FACING, Direction.DOWN)
                .setValue(PRECISE, false)
                .setValue(POWERED, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(MemoryBlockComparatorBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(PRECISE).add(POWERED);
    }

    @Override
    public VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return switch (state.getValue(FACING)) {
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AddonBlockEntities.MEMORY_BLOCK_COMPARATOR.create(pos, state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            facing = facing.getOpposite();
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.isClientSide) return;
        if (!oldState.is(state.getBlock()) && level.getBlockEntity(pos) instanceof MemoryBlockComparatorBlockEntity be) {
            BlockState frontState = level.getBlockState(pos.relative(state.getValue(FACING)));
            be.setRememberedState(frontState);
        }
        level.scheduleTick(pos, this, 1);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.PASS;
        }
        BlockState newState = state.cycle(PRECISE);
        level.setBlock(pos, newState, Block.UPDATE_ALL);
        this.updateNeighborsInFront(level, pos, state);
        return InteractionResult.sidedSuccess(level.isClientSide);
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
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        if (player instanceof ServerPlayer serverPlayer) {
            if (level.getBlockEntity(pos) instanceof MemoryBlockComparatorBlockEntity be) {
                ItemStack held = serverPlayer.getItemInHand(hand);

                // Regular disk handling (IDiskCloneable)
                if (held.is(ModItems.DISK)) {
                    return Util.interactionResultConverter()
                        .apply(be.useDisk(level, serverPlayer, hand, held, hitResult));
                }

                // 结构磁盘处理
                if (held.is(ModItems.STRUCTURE_DISK)) {
                    return handleStructureDisk(level, pos, state, serverPlayer, held, be, this);
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block neighborBlock,
        BlockPos neighborPos,
        boolean movedByPiston
    ) {
        if (!level.isClientSide && neighborPos.equals(pos.relative(state.getValue(FACING)))) {
            if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                level.scheduleTick(pos, this, 2);
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean matches = checkMatch(level, pos, state);
        if (matches != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, matches), Block.UPDATE_ALL);
            this.updateNeighborsInFront(level, pos, state);
        }
    }

    private boolean checkMatch(Level level, BlockPos pos, BlockState state) {
        if (!(level.getBlockEntity(pos) instanceof MemoryBlockComparatorBlockEntity be)) {
            return false;
        }
        Direction facing = state.getValue(FACING);
        BlockState current = level.getBlockState(pos.relative(facing));
        BlockState remembered = be.getRememberedState();
        return state.getValue(PRECISE)
               ? current.equals(remembered)
               : current.is(remembered.getBlock());
    }

    /**
     * 处理结构磁盘与记忆方块比较器的交互。
     * 若磁盘已有数据则加载，否则保存为 1x1x1 结构。
     */
    protected static ItemInteractionResult handleStructureDisk(
        Level level,
        BlockPos pos,
        BlockState state,
        ServerPlayer serverPlayer,
        ItemStack held,
        MemoryBlockComparatorBlockEntity be,
        Block block
    ) {
        // 磁盘已有数据 → 加载结构
        if (held.has(ModComponents.STRUCTURE_DISK_DATA)) {
            StructureLoadUtil.StructureData structureData = StructureLoadUtil.loadStructureFromDisk(level, held);
            if (structureData == null || structureData.blocks.isEmpty()) {
                return ItemInteractionResult.FAIL;
            }
            if (structureData.blocks.size() > 1) {
                serverPlayer.displayClientMessage(
                    Component.translatable("block.anvilcraft_pigsplus.memory_block_comparator.structure_too_large")
                        .withStyle(ChatFormatting.RED),
                    true
                );
                return ItemInteractionResult.SUCCESS;
            }
            // 单个方块，直接应用
            be.setRememberedState(structureData.blocks.getFirst().state());
            level.scheduleTick(pos, block, 2);
            return ItemInteractionResult.SUCCESS;
        } else {
            // 空磁盘 → 将记忆的方块状态保存为 1x1x1 结构
            BlockState remembered = be.getRememberedState();

            CompoundTag tag = StructureDiskUtil.getCompoundTag(remembered);

            return StructureDiskUtil.saveData(level, state.getValue(MemoryBlockComparatorBlock.FACING), held, tag)
                   ? ItemInteractionResult.SUCCESS
                   : ItemInteractionResult.FAIL;
        }
    }

    protected void updateNeighborsInFront(Level level, BlockPos pos, BlockState state) {
        Direction outputDirection = state.getValue(FACING).getOpposite();
        BlockPos blockPos = pos.relative(outputDirection);
        level.neighborChanged(blockPos, this, pos);
        level.updateNeighborsAtExceptFromFacing(blockPos, this, outputDirection);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction != (state.getValue(FACING).getOpposite());
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getSignal(blockAccess, pos, side);
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWERED) && blockState.getValue(FACING).getOpposite() != side ? 15 : 0;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }
}
