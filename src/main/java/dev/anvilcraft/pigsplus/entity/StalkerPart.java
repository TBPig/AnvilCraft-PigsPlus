package dev.anvilcraft.pigsplus.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.entity.PartEntity;

public abstract class StalkerPart extends PartEntity<StalkerEntity> {
    protected EntityDimensions realSize = EntityDimensions.fixed(1.0F, 1.0F);

    public StalkerPart(StalkerEntity parent, float w, float h) {
        super(parent);
        this.setSize(EntityDimensions.scalable(w, h));
    }

    @Override
    public void tick() {
        this.moveTo(this.getX(), this.getY(), this.getZ());
        super.tick();
        this.clearFire();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return this.getParent().attackEntityFromPart(this, source, amount);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canUsePortal(boolean force) {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    protected void setSize(EntityDimensions size) {
        this.realSize = size;
        this.refreshDimensions();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.realSize;
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return this.getParent().isCurrentlyGlowing();
    }

    @Override
    public boolean isInvisible() {
        return this.getParent().isInvisible();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }
}
