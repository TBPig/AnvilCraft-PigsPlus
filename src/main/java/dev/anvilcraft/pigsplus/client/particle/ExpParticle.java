package dev.anvilcraft.pigsplus.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class ExpParticle extends SingleQuadParticle {
    public static final int LIFE_TIME = 20;

    public ExpParticle(
        ClientLevel level, double x, double y, double z,
        double xd, double yd, double zd,
        TextureAtlasSprite sprite
    ) {
        super(level, x, y, z, sprite);
        this.hasPhysics = false;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.lifetime = LIFE_TIME;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(
            SimpleParticleType options,
            ClientLevel level,
            double x,
            double y,
            double z,
            double auxX,
            double auxY,
            double auxZ,
            RandomSource random
        ) {
            return new ExpParticle(level, x, y, z, auxX, auxY, auxZ, this.sprites.get(random));
        }
    }
}
