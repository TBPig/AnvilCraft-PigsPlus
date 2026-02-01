package dev.anvilcraft.pigsplus.block;

import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AutoChickenBlock extends Block implements IHammerRemovable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static VoxelShape SHAPE = Shapes.or(
        Block.box(0, 14, 0, 16, 16, 16),
        Block.box(2, 2, 2, 14, 14, 14),
        Block.box(0, 0, 0, 16, 2, 16)
    );

    public AutoChickenBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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

    public void spawnEgg(ServerLevel level, BlockPos pos, float fallDistance) {
        BlockState breakerBlockState = level.getBlockState(pos);
        if (breakerBlockState.isAir()) return;
        RandomSource randomSource = level.getRandom();
        float f = randomSource.nextFloat();
        if (fallDistance <= 1.25f) {
            fallDistance = 1.25f;
        }
        if (f <= (1 / fallDistance)) {
            return;
        }
        // 在方块下方生成一个鸡蛋掉落物
        ItemStack itemStack = new ItemStack(Items.EGG);
        Vec3 itemPos = pos.below().getCenter();
        level.addFreshEntity(
            new ItemEntity(
                level,
                itemPos.x,
                itemPos.y,
                itemPos.z,
                itemStack,
                level.random.nextDouble() * 0.2 - 0.1,
                -0.5,
                level.random.nextDouble() * 0.2 - 0.1
            )
        );
        // 产生鸡下蛋的声音
        level.playSound(
            null,
            pos,
            SoundEvents.CHICKEN_EGG,
            SoundSource.BLOCKS,
            0.7F,
            level.random.nextFloat() * 0.2F + 0.9F
        );
    }
}
