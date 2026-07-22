package dev.anvilcraft.pigsplus.util;

import dev.anvilcraft.pigsplus.init.AddonParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class ParticleUtil {
    public static final double EXP_PARTICLE_SPEED = 0.025;

    public static void sendParticle(ServerLevel level, BlockPos startPos, BlockPos endPos) {
        sendParticle(level, startPos.getCenter(), endPos.getCenter());
    }

    public static void sendParticle(ServerLevel level, Vec3 startPos, Vec3 endPos) {
        Vec3 start = startPos.offsetRandom(level.getRandom(), 0.3f);
        Vec3 end = endPos.offsetRandom(level.getRandom(), 0.3f);
        Vec3 offset = start.vectorTo(end);
        double distance = offset.length();
        sendParticle(level, start, offset, distance * EXP_PARTICLE_SPEED);
    }

    public static void sendParticle(ServerLevel level, Vec3 start, Vec3 offset, double speed) {
        level.sendParticles(
            AddonParticleTypes.EXP.get(),
            start.x(),
            start.y(),
            start.z(),
            0,
            offset.x(),
            offset.y(),
            offset.z(),
            speed
        );
    }
}
