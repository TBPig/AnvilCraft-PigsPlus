package dev.anvilcraft.pigsplus.util;

import dev.dubhe.anvilcraft.AnvilCraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidType;

public class ExpUtil {
    public static int getExperienceToLiquid() {
        // Round up so fractional mB costs cannot create experience.
        return Math.ceilDiv(FluidType.BUCKET_VOLUME, Math.max(1, AnvilCraft.CONFIG.world.expFluidXpPerBlock));
    }

    public static int getFLuidFromXp(int xp) {
        if (AnvilCraft.CONFIG.world.expFluidXpPerBlock <= 0) return Integer.MAX_VALUE;
        return (int) Math.min(Integer.MAX_VALUE, (long) getExperienceToLiquid() * xp);
    }

    public static int getXpFromFluid(int fluid) {
        if (AnvilCraft.CONFIG.world.expFluidXpPerBlock <= 0) return 0;
        return fluid / getExperienceToLiquid();
    }

    public static int XpRound(int fluid) {
        if (AnvilCraft.CONFIG.world.expFluidXpPerBlock <= 0) return 0;
        int experienceToLiquid = getExperienceToLiquid();
        return fluid - fluid % experienceToLiquid;
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
