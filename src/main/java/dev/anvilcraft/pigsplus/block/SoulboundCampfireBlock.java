package dev.anvilcraft.pigsplus.block;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.pigsplus.block.entity.SoulboundCampfireBlockEntity;
import dev.anvilcraft.pigsplus.event.SoulboundCampfireEventListener;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 缠魂营火
 *
 * <p>由点燃的灵魂营火根据周围缠怨藤的数量有概率转化而来；工作时蚕食周围作物的生长层数，
 * 为周围玩家提供生命恢复，并可作为“灵魂蔓生”配方的触发方块</p>
 *
 * @see SoulboundCampfireEventListener 转化逻辑
 */
public class SoulboundCampfireBlock extends CampfireBlock {
    public static final MapCodec<CampfireBlock> CODEC = simpleCodec(
        properties -> new SoulboundCampfireBlock(true, 4, properties)
    );

    public SoulboundCampfireBlock(boolean spawnParticles, int fireDamage, Properties properties) {
        super(spawnParticles, fireDamage, properties);
    }

    @Override
    public MapCodec<CampfireBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AddonBlockEntities.SOULBOUND_CAMPFIRE.create(pos, state);
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
        if (!(level.getBlockEntity(pos) instanceof SoulboundCampfireBlockEntity campfire)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack held = player.getItemInHand(hand);
        Optional<RecipeHolder<CampfireCookingRecipe>> recipe = campfire.getCookableRecipe(held);
        if (recipe.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide() && campfire.placeFood(player, held, recipe.get().value().getCookingTime())) {
            player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())
            && level.getBlockEntity(pos) instanceof SoulboundCampfireBlockEntity campfire) {
            Containers.dropContents(level, pos, campfire.getItems());
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        BlockState state,
        BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return state.getValue(LIT)
                ? createTickerHelper(type, AddonBlockEntities.SOULBOUND_CAMPFIRE.get(), SoulboundCampfireBlockEntity::particleTick)
                : null;
        }
        return state.getValue(LIT)
            ? createTickerHelper(type, AddonBlockEntities.SOULBOUND_CAMPFIRE.get(), SoulboundCampfireBlock::serverTick)
            : createTickerHelper(type, AddonBlockEntities.SOULBOUND_CAMPFIRE.get(), SoulboundCampfireBlockEntity::cooldownTick);
    }

    private static void serverTick(Level level, BlockPos pos, BlockState state, SoulboundCampfireBlockEntity campfire) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        SoulboundCampfireBlockEntity.cookTick(serverLevel, pos, state, campfire);
        SoulboundCampfireBlockEntity.soulTick(serverLevel, pos, state, campfire);
        SoulboundCampfireBlockEntity.auraTick(serverLevel, pos, campfire);
    }
}
