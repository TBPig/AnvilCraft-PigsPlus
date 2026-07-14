package dev.anvilcraft.pigsplus.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MathUtil {
    public static int getCount(float probability, int num, Level level) {
        float totalProbability = probability * num;
        int baseCount = (int) totalProbability;
        float fractionalPart = totalProbability - baseCount;

        int count = baseCount;
        if (level.getRandom().nextFloat() < fractionalPart) {
            count++;
        }
        return count;
    }

    public static Vec3 copy(Vec3 source) {
        return new Vec3(source.x, source.y, source.z);
    }

}
