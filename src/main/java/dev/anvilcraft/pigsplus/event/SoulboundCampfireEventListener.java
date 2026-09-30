package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent.BlockToolModificationEvent;

/**
 * 缠魂营火的转化逻辑
 *
 * <p>点燃灵魂营火时，根据周围缠怨藤的数量有概率将其转化为缠魂营火</p>
 */
@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class SoulboundCampfireEventListener {
    /**
     * 转化判定搜索缠怨藤的半径（水平与竖直方向）
     */
    private static final int CONVERT_VINE_RADIUS = 2;

    @SubscribeEvent
    public static void onToolModification(BlockToolModificationEvent event) {
        if (event.isSimulated() || event.getItemAbility() != ItemAbilities.FIRESTARTER_LIGHT) {
            return;
        }
        BlockState state = event.getState();
        if (!state.is(Blocks.SOUL_CAMPFIRE) || state.getValue(CampfireBlock.LIT) || state.getValue(CampfireBlock.WATERLOGGED)) {
            return;
        }
        if (!(event.getLevel() instanceof Level level) || level.isClientSide()) {
            return;
        }


        BlockPos pos = event.getPos();
        int vines = countWeepingVines(level, pos);
        if (vines <= 0) {
            return;
        }
        double chance = Math.min(1.0, vines * AnvilCraftPigsPlus.CONFIG.soulboundCampfireConvertChancePerVine);
        if (level.getRandom().nextDouble() < chance) {
            event.setFinalState(
                AddonBlocks.SOULBOUND_CAMPFIRE.get()
                    .defaultBlockState()
                    .setValue(CampfireBlock.FACING, state.getValue(CampfireBlock.FACING))
                    .setValue(CampfireBlock.LIT, true)
                    .setValue(CampfireBlock.SIGNAL_FIRE, state.getValue(CampfireBlock.SIGNAL_FIRE))
                    .setValue(CampfireBlock.WATERLOGGED, state.getValue(CampfireBlock.WATERLOGGED))
            );
            level.playSound(null, pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 0.7F, 1.0F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    24,
                    0.4,
                    0.4,
                    0.4,
                    0.02
                );
            }
        }
    }

    public static int countWeepingVines(Level level, BlockPos pos) {
        int vines = 0;
        for (BlockPos target : BlockPos.betweenClosed(
            pos.offset(-CONVERT_VINE_RADIUS, -CONVERT_VINE_RADIUS, -CONVERT_VINE_RADIUS),
            pos.offset(CONVERT_VINE_RADIUS, CONVERT_VINE_RADIUS, CONVERT_VINE_RADIUS)
        )) {
            BlockState state = level.getBlockState(target);
            if (state.is(Blocks.TWISTING_VINES) || state.is(Blocks.TWISTING_VINES_PLANT)) {
                vines++;
            }
        }
        return vines;
    }
}