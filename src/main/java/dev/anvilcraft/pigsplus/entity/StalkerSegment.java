package dev.anvilcraft.pigsplus.entity;

import lombok.Getter;
import net.minecraft.world.entity.EntityDimensions;

public class StalkerSegment extends StalkerPart {
    @Getter
    private final int tentacleIndex;
    @Getter
    private final int segmentIndex;

    public StalkerSegment(StalkerEntity parent, int tentacleIndex, int segmentIndex) {
        super(parent, 0.8F, 0.8F);
        this.tentacleIndex = tentacleIndex;
        this.segmentIndex = segmentIndex;
    }

    public float getScale() {
        return this.getParent().getSegmentScale(this.tentacleIndex);
    }

    @Override
    public boolean isPickable() {
        return this.getParent().isTentacleAlive(this.tentacleIndex);
    }

    @Override
    public void tick() {
        super.tick();

        float scale = this.getScale();
        float s = 0.8F * Math.max(scale, 0.01F);
        this.setSize(EntityDimensions.scalable(s, s));
    }

    public void killByAnvil() {
        this.getParent().killTentacle(this.tentacleIndex);
    }
}
