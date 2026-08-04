package dev.anvilcraft.pigsplus.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.lib.v2.util.ShapeUtil;
import dev.anvilcraft.pigsplus.block.entity.WirelessTransmitterBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.dubhe.anvilcraft.api.hammer.HammerRotateBehavior;
import dev.dubhe.anvilcraft.api.hammer.IHammerChangeable;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.block.better.BetterBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class WirelessTransmitterBlock extends BetterBaseEntityBlock implements IHammerRemovable, IHammerChangeable {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty OVERLOAD = IPowerComponent.OVERLOAD;
    public static final VoxelShape SHAPE = Shapes.or(
        Block.box(0, 0, 0, 16, 4, 16),
        Block.box(4, 4, 4, 12, 6, 12)
    );
    public static final VoxelShape UP_MODEL = SHAPE;
    public static final VoxelShape DOWN_MODEL = ShapeUtil.rotate(Direction.Axis.X, 180, UP_MODEL);
    public static final VoxelShape NORTH_MODEL = ShapeUtil.rotate(Direction.Axis.X, 90, UP_MODEL);
    public static final VoxelShape WEST_MODEL = ShapeUtil.rotate(Direction.Axis.Y, 90, NORTH_MODEL);
    public static final VoxelShape SOUTH_MODEL = ShapeUtil.rotate(Direction.Axis.Y, 180, NORTH_MODEL);
    public static final VoxelShape EAST_MODEL = ShapeUtil.rotate(Direction.Axis.Y, 270, NORTH_MODEL);

    public WirelessTransmitterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(OVERLOAD, true));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WirelessTransmitterBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OVERLOAD);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for (Direction backDirection : context.getNearestLookingDirections()) {
            BlockPos backPos = context.getClickedPos().relative(backDirection);
            if (hasContainerCapability(context.getLevel(), backPos, backDirection.getOpposite())) {
                return this.defaultBlockState()
                    .setValue(FACING, backDirection.getOpposite())
                    .setValue(OVERLOAD, true);
            }
        }
        return this.defaultBlockState()
            .setValue(FACING, context.getNearestLookingDirection().getOpposite())
            .setValue(OVERLOAD, true);
    }

    public static boolean hasContainerCapability(Level level, BlockPos pos, Direction context) {
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, context) != null
            || level.getCapability(Capabilities.FluidHandler.BLOCK, pos, context) != null;
    }

    @Override
    public boolean change(Player player, BlockPos pos, Level level, ItemStack anvilHammer) {
        return HammerRotateBehavior.DEFAULT.change(player, pos, level, anvilHammer);
    }

    @Override
    public Property<?> getChangeableProperty(BlockState blockState) {
        return FACING;
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WirelessTransmitterBlockEntity(AddonBlockEntities.WIRELESS_TRANSMITTER.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(
            type,
            AddonBlockEntities.WIRELESS_TRANSMITTER.get(),
            (serverLevel, blockPos, blockState, blockEntity) -> blockEntity.tick(serverLevel, blockPos)
        );
    }
}
