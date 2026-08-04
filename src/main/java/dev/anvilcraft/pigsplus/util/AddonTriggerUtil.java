package dev.anvilcraft.pigsplus.util;

import dev.anvilcraft.pigsplus.init.AddonCriterionTriggers;
import dev.dubhe.anvilcraft.util.PlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class AddonTriggerUtil {
    private static final int PIG_ANVIL_TRIGGER_RADIUS = 8;

    public static void pigAnvilTransform(Level level, BlockPos pos, boolean triple) {
        if (level.isClientSide()) return;
        for (ServerPlayer player : PlayerUtil.searchPlayerByPos(level, pos, PIG_ANVIL_TRIGGER_RADIUS)) {
            AddonCriterionTriggers.PIG_ANVIL_TRANSFORM.get().trigger(player, triple);
            player.getAdvancements().flushDirty(player);
        }
    }

    private AddonTriggerUtil() {
    }
}
