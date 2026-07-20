package dev.anvilcraft.pigsplus.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.pigsplus.block.entity.PecisionMagneticPivotBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.block.better.BetterBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PecisionMagneticPivotBlock extends BetterBaseEntityBlock implements IHammerRemovable {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static VoxelShape SHAPE = Shapes.or(
        Block.box(0, 0, 0, 16, 2, 16),
        Block.box(3, 0, 3, 13, 13, 13),
        Block.box(4, 0, 4, 12, 16, 12)
    );

    public PecisionMagneticPivotBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(PecisionMagneticPivotBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(
        BlockState state,
        BlockGetter level,
        BlockPos pos,
        CollisionContext context
    ) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PecisionMagneticPivotBlockEntity(AddonBlockEntities.PRECISION_MAGNETIC_PIVOT.get(), pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(
            type, AddonBlockEntities.PRECISION_MAGNETIC_PIVOT.get(),
            (level1, pos, state1, blockEntity) -> blockEntity.tick((ServerLevel) level1, pos)
        );
    }
}
