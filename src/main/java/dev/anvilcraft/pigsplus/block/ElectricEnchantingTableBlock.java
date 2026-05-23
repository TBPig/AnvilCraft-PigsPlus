package dev.anvilcraft.pigsplus.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.dubhe.anvilcraft.api.hammer.IHammerRemovable;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElectricEnchantingTableBlock extends BaseEntityBlock implements IHammerRemovable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OVERLOAD = IPowerComponent.OVERLOAD;

    public static final List<BlockPos> BOOKSHELF_OFFSETS =
        BlockPos.betweenClosedStream(-2, 0, -2, 2, 1, 2)
            .filter((blockPos) -> Math.abs(blockPos.getX()) == 2 || Math.abs(blockPos.getZ()) == 2)
            .map(BlockPos::immutable)
            .toList();

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
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(
            type,
            AddonBlockEntities.ELECTRIC_ENCHANTING_TABLE.get(),
            (level1, blockPos, _, blockEntity) -> blockEntity.tick(level1, blockPos)
        );
    }

    @Override
    protected void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block block,
        @Nullable Orientation orientation,
        boolean movedByPiston
    ) {
        if (level.isClientSide()) return;
        level.setBlock(pos, state.setValue(POWERED, level.hasNeighborSignal(pos)), Block.UPDATE_CLIENTS);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ElectricEnchantingTableBlockEntity(AddonBlockEntities.ELECTRIC_ENCHANTING_TABLE.get(), blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED).add(OVERLOAD);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        for (BlockPos blockpos : BOOKSHELF_OFFSETS) {
            BlockPos bookshelfPos = pos.offset(blockpos);
            if (random.nextInt(16) == 0 && level.getBlockState(bookshelfPos).getEnchantPowerBonus(level, bookshelfPos) != 0.0F) {
                level.addParticle(
                    ParticleTypes.ENCHANT,
                    (double) pos.getX() + (double) 0.5F,
                    (double) pos.getY() + (double) 2.0F,
                    (double) pos.getZ() + (double) 0.5F,
                    (double) ((float) blockpos.getX() + random.nextFloat()) - (double) 0.5F,
                    (float) blockpos.getY() - random.nextFloat() - 1.0F,
                    (double) ((float) blockpos.getZ() + random.nextFloat()) - (double) 0.5F
                );
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof ElectricEnchantingTableBlockEntity table ? table.getAnalogRedstoneSignal() : 0;
    }

    @Override
    protected InteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit
    ) {
        if (level.getBlockEntity(pos) instanceof ElectricEnchantingTableBlockEntity table) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            // 玩家空手时尝试取出物品
            if (stack.isEmpty()) {
                ItemResource resourceIn = table.getFilteredItemStackHandler().getResource(2);
                if (!resourceIn.isEmpty()) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        int extracted = table.getFilteredItemStackHandler().extract(2, resourceIn, Integer.MAX_VALUE, transaction);
                        if (extracted == 0) return super.useItemOn(stack, state, level, pos, player, hand, hit);
                        transaction.commit();
                        player.getInventory().placeItemBackInInventory(resourceIn.toStack(extracted));
                        level.playSound(
                            null,
                            pos,
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.PLAYERS,
                            .2F,
                            1F + level.getRandom().nextFloat()
                        );
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                try (Transaction transaction = Transaction.openRoot()) {
                    int inserted = table.getFilteredItemStackHandler().insert(0, ItemResource.of(stack), stack.getCount(), transaction);
                    if (inserted == 0) return super.useItemOn(stack, state, level, pos, player, hand, hit);
                    transaction.commit();
                    stack.shrink(inserted);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}
