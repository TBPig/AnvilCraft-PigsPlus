package dev.anvilcraft.pigsplus.util;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.entity.fakeplayer.AnvilCraftFakePlayers;
import dev.dubhe.anvilcraft.init.block.ModBlockTags;
import dev.dubhe.anvilcraft.util.BlockMiningEffect;
import dev.dubhe.anvilcraft.util.BreakBlockUtil;
import dev.dubhe.anvilcraft.util.DevourUtil;
import dev.dubhe.anvilcraft.util.PistonMoveGuard;
import dev.dubhe.anvilcraft.util.TriggerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BlockBreakerUtil {
    private BlockBreakerUtil() {
    }

    public static BreakResult breakBlock(
        ServerLevel level,
        BlockPos pos,
        BlockMiningEffect miningEffect,
        @Nullable Player player,
        boolean protectContainers,
        boolean collectDrops
    ) {
        if (PistonMoveGuard.isReserved(level, pos)) return BreakResult.notDestroyed();

        BlockState state = level.getBlockState(pos);
        if (!DevourUtil.shouldDevour(state)) return BreakResult.notDestroyed();
        if (protectContainers && isItemHandlerContainer(level, pos)) return BreakResult.notDestroyed();

        if (
            !miningEffect.isDisintegration()
            && state.is(ModBlockTags.BLOCK_DEVOURER_PROBABILITY_DROPPING)
            && level.random.nextDouble() > AnvilCraft.CONFIG.world.blockDevourerDropSuppressionChance
        ) {
            return level.destroyBlock(pos, false)
                   ? BreakResult.destroyed(List.of())
                   : BreakResult.notDestroyed();
        }

        if (miningEffect.isDisintegration()) {
            BreakBlockUtil.dropExperience(level, pos, state, miningEffect);
        }

        List<ItemStack> drops = new ArrayList<>();
        if (collectDrops) {
            drops.addAll(transferLecternBook(level, pos));
            if (!miningEffect.isDisintegration()) {
                drops.addAll(BreakBlockUtil.drop(level, pos, miningEffect));
            }
        }

        destroyBlock(level, pos, state, player);
        if (collectDrops && !miningEffect.isDisintegration()) {
            drops.addAll(collectItemDrops(level, pos));
        }
        TriggerUtil.devourerDevourBlock(level, pos, state.getBlock());
        return BreakResult.destroyed(drops);
    }

    public static boolean isItemHandlerContainer(ServerLevel level, BlockPos pos) {
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null;
    }

    public static List<ItemStack> transferLecternBook(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof LecternBlockEntity lectern)) return List.of();
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) return List.of();
        lectern.setBook(ItemStack.EMPTY);
        return List.of(book);
    }

    private static void destroyBlock(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player player) {
        if (!(state.getBlock() instanceof DoublePlantBlock)) {
            if (player == null) {
                ServerPlayer fakePlayer = AnvilCraftFakePlayers.getBlockPlacer().offerPlayer(level);
                try {
                    state.getBlock().playerWillDestroy(level, pos, state, fakePlayer);
                } finally {
                    AnvilCraftFakePlayers.getBlockPlacer().disable(fakePlayer);
                }
            } else {
                state.getBlock().playerWillDestroy(level, pos, state, player);
            }
        }
        level.destroyBlock(pos, false);
    }

    private static List<ItemStack> collectItemDrops(ServerLevel level, BlockPos pos) {
        List<ItemStack> drops = new ArrayList<>();
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos))) {
            if (!itemEntity.blockPosition().equals(pos)) continue;
            ItemStack stack = itemEntity.getItem();
            if (!stack.isEmpty()) drops.add(stack);
            itemEntity.discard();
        }
        return drops;
    }

    public record BreakResult(boolean destroyed, List<ItemStack> drops) {
        public BreakResult {
            drops = List.copyOf(drops);
        }

        public static BreakResult notDestroyed() {
            return new BreakResult(false, List.of());
        }

        public static BreakResult destroyed(List<ItemStack> drops) {
            return new BreakResult(true, drops);
        }
    }
}
