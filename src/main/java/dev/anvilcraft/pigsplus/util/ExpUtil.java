package dev.anvilcraft.pigsplus.util;

public class ExpUtil {
    public static final int EXPERIENCE_TO_LIQUID = 20;
    public static final int XP_LEVEL_NUM = 3;
    public static final int[] XP_LEVEL_N = {
        30,
        15,
        0
    };
    public static final int[] XP_LEVEL_A = {
        9,
        5,
        2
    };
    public static final int[] XP_LEVEL_B = {
        112,
        37,
        7
    };

    public static int getFLuidFromXp(int xp) {
        return EXPERIENCE_TO_LIQUID * xp;
    }

    public static int getXpFromFluid(int fluid) {
        return fluid / EXPERIENCE_TO_LIQUID;
    }

    public static int XpRound(int fluid) {
        return fluid - fluid % EXPERIENCE_TO_LIQUID;
    }

    public static int getXpfromLevel(int level) {
        for (int i = 0; i < XP_LEVEL_NUM; i++) {
            if (level >= XP_LEVEL_N[i]) {
                return level * XP_LEVEL_A[i] + XP_LEVEL_B[i];
            }
        }
        return 0;
    }

    public static int getXpfromAllLevel(int level) {
        int xp = 0;
        for (int i = 0; i < XP_LEVEL_NUM; i++) {
            if (level >= XP_LEVEL_N[i]) {
                xp += (getXpfromLevel(level) + getXpFromFluid(XP_LEVEL_N[i])) * (level - XP_LEVEL_N[i] + 1) / 2;
                level = XP_LEVEL_N[i] - 1;
            }
        }
        return xp;
    }
}
