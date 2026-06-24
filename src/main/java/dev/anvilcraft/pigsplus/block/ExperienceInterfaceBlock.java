package dev.anvilcraft.pigsplus.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.lib.v2.util.ShapeUtil;
import dev.anvilcraft.pigsplus.block.entity.ExperienceInterfaceBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.network.ExperienceInterfaceInitPacket;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.block.better.BetterBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ExperienceInterfaceBlock extends BetterBaseEntityBlock implements IHammerRemovable, EntityBlock {
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

    public static final VoxelShape UP_MODEL = Block.box(1.0, 0.0, 1.0, 15.0, 3.0, 15.0);
    public static final VoxelShape DOWN_MODEL = ShapeUtil.rotate(Direction.Axis.X, 180, UP_MODEL);
    public static final VoxelShape NORTH_MODEL = ShapeUtil.rotate(Direction.Axis.X, 90, UP_MODEL);
    public static final VoxelShape WEST_MODEL = ShapeUtil.rotate(Direction.Axis.Y, 90, NORTH_MODEL);
    public static final VoxelShape SOUTH_MODEL = ShapeUtil.rotate(Direction.Axis.Y, 180, NORTH_MODEL);
    public static final VoxelShape EAST_MODEL = ShapeUtil.rotate(Direction.Axis.Y, 270, NORTH_MODEL);

    public ExperienceInterfaceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ExperienceInterfaceBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getNearestLookingDirection().getOpposite();
        for (Direction direction : context.getNearestLookingDirections()) {
            BlockState state = this.defaultBlockState().setValue(FACING, direction.getOpposite());
            if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                return state;
            }
        }
        return this.defaultBlockState().setValue(FACING, dir);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return FaceAttachedHorizontalDirectionalBlock.canAttach(level, pos, state.getValue(FACING).getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> UP_MODEL;
            case NORTH -> NORTH_MODEL;
            case WEST -> WEST_MODEL;
            case SOUTH -> SOUTH_MODEL;
            case EAST -> EAST_MODEL;
            default -> DOWN_MODEL;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof ExperienceInterfaceBlockEntity entity
            && player instanceof ServerPlayer serverPlayer) {
            AddonMenuTypes.open(serverPlayer, entity, pos);
            PacketDistributor.sendToPlayer(serverPlayer, new ExperienceInterfaceInitPacket(entity.getXpTarget()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExperienceInterfaceBlockEntity(AddonBlockEntities.EXPERIENCE_INTERFACE.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        } else {
            return createTickerHelper(
                type, AddonBlockEntities.EXPERIENCE_INTERFACE.get(),
                (lv, blockPos, blockState, blockEntity) -> blockEntity.tick(lv)
            );
        }
    }
}

