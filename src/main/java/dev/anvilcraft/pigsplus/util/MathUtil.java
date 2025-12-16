package dev.anvilcraft.pigsplus.util;

import net.minecraft.world.level.Level;

public class MathUtil {
    public static int getCount(float probability, int num, Level level) {
        float totalProbability = probability * num;
        int baseCount = (int) totalProbability;
        float fractionalPart = totalProbability - baseCount;

        int count = baseCount;
        if (level.random.nextFloat() < fractionalPart) {
            count++;
        }
        return count;
    }
}
