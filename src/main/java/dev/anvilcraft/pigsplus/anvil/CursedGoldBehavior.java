package dev.anvilcraft.pigsplus.anvil;

import dev.anvilcraft.pigsplus.init.AddonEntities;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.api.anvil.IAnvilBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CursedGoldBehavior implements IAnvilBehavior {
    private static final double SEARCH_RADIUS = 1.5;

    @Override
    public boolean handle(Level level, BlockPos hitBlockPos, BlockState hitBlockState, float fallDistance, AnvilEvent.OnLand event) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        //  是否有回响晶洞
        AABB searchBox = new AABB(hitBlockPos.above()).inflate(SEARCH_RADIUS);
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, searchBox)) {
            ItemStack stack = itemEntity.getItem();
            if (!stack.is(AddonItems.ECHO_GEODE.get()) || itemEntity.isRemoved()) continue;

            // 消耗物品
            stack.shrink(1);
            if (stack.isEmpty()) {
                itemEntity.discard();
            }
            // 生成怪物
            spawnStalker(serverLevel, hitBlockPos);
            return true;

        }
        return false;
    }

    private static void spawnStalker(ServerLevel level, BlockPos pos) {
        // Try to spawn 2 blocks above the cursed gold block
        BlockPos spawnPos = pos.above(2);
        if (!level.getBlockState(spawnPos).isAir() || !level.getBlockState(spawnPos.above()).isAir()) {
            spawnPos = pos.above();
        }

        Monster stalker = AddonEntities.STALKER.get().create(level);
        if (stalker != null) {
            stalker.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            level.addFreshEntity(stalker);
            level.playSound(null, pos, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 2.0f, 0.5f);
            level.playSound(null, pos, SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 2.0f, 0.8f);
        }
    }
}
