package dev.anvilcraft.pigsplus.util;

import net.minecraft.world.entity.player.Player;

public class ExpUtil {
    public static final int EXPERIENCE_TO_LIQUID = 20;

    public static int getFLuidFromXp(int xp) {
        return EXPERIENCE_TO_LIQUID * xp;
    }

    public static int getXpFromFluid(int fluid) {
        return fluid / EXPERIENCE_TO_LIQUID;
    }

    public static int XpRound(int fluid) {
        return fluid - fluid % EXPERIENCE_TO_LIQUID;
    }

    public static int getXpfromAllLevel(int level) {
        if (level == 0) {
            return 0;
        }
        if (level > 0 && level < 16) {
            return level * (12 + level * 2) / 2;
        } else if (level > 15 && level < 31) {
            return (level - 15) * (69 + (level - 15) * 5) / 2 + 315;
        } else {
            return (int) Math.min(Integer.MAX_VALUE, (level - 30L) * (215 + (level - 30) * 9L) / 2 + 1395);
        }
    }

    public static int getPlayerXp(Player player) {
        return (int) Math.min(
            Integer.MAX_VALUE,
            getXpfromAllLevel(player.experienceLevel) + ((long) (player.experienceProgress * player.getXpNeededForNextLevel()))
        );
    }
}
