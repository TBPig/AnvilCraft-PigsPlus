package dev.anvilcraft.pigsplus.entity;

import dev.anvilcraft.pigsplus.util.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StalkerEntity extends Monster {
    public static final int TENTACLE_NUM = 8;
    public static final int SEGMENTS_PER_TENTACLE = 5;
    public static final float TENTACLE_HEALTH_RATIO = 0.5f;
    public static final int CONVERSION_COOLDOWN = 20;

    public static final List<BlockPos> CONVERT_RANGE =
        BlockPos.betweenClosedStream(-1, -1, -1, 1, 1, 1)
            .map(BlockPos::immutable)
            .toList();

    private static final EntityDataAccessor<Byte> ID_TENTACLES = SynchedEntityData.defineId(
        StalkerEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ID_TENTACLE_HURT_TIMES = SynchedEntityData.defineId(
        StalkerEntity.class, EntityDataSerializers.INT);

    public final StalkerSegment[][] stalkerSegments = new StalkerSegment[TENTACLE_NUM][SEGMENTS_PER_TENTACLE];
    public final StalkerBody body;
    private final PartEntity<?>[] parts;

    private final byte[] tentacleHealth = new byte[TENTACLE_NUM];
    private final byte[] tentacleHurtTime = new byte[TENTACLE_NUM];
    private int sculkConversionCooldown = 0;

    public StalkerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);

        this.body = new StalkerBody(this);

        List<PartEntity<?>> partList = new ArrayList<>(TENTACLE_NUM * SEGMENTS_PER_TENTACLE + 1);
        partList.add(this.body);
        for (int t = 0; t < TENTACLE_NUM; t++) {
            this.tentacleHealth[t] = (byte) (this.getMaxHealth() * TENTACLE_HEALTH_RATIO);
            for (int s = 0; s < SEGMENTS_PER_TENTACLE; s++) {
                StalkerSegment seg = new StalkerSegment(this, t, s);
                this.stalkerSegments[t][s] = seg;
                partList.add(seg);
            }
        }
        this.parts = partList.toArray(new PartEntity<?>[0]);

        this.noCulling = true;
        this.xpReward = 100;
        this.setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.parts.length; i++) {
            this.parts[i].setId(id + i + 1);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 100.0)
            .add(Attributes.ATTACK_DAMAGE, 16.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_TENTACLES, (byte) ((1 << TENTACLE_NUM) - 1));
        builder.define(ID_TENTACLE_HURT_TIMES, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }
    // ========== Multipart Entity ==========

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Nullable
    @Override
    public PartEntity<?>[] getParts() {
        return this.parts;
    }

    public boolean attackEntityFromPart(StalkerPart part, DamageSource source, float amount) {
        if (source.getEntity() == this) return false;
        if (this.level().isClientSide()) return false;

        if (part instanceof StalkerBody) {
            return this.hurt(source, amount);
        } else if (part instanceof StalkerSegment seg) {
            int t = seg.getTentacleIndex();

            if (this.tentacleHealth[t] <= 0) return false;

            this.hurt(source, amount);

            if (source.is(DamageTypes.FALLING_ANVIL)) {
                killTentacle(t);
                return true;
            }
            int damage = Math.max(1, Math.round(amount));
            this.tentacleHealth[t] = (byte) Math.max(0, this.tentacleHealth[t] - damage);
            this.tentacleHurtTime[t] = 8;
            this.syncTentacleHurtTimes();
            this.syncTentacleData();
            if (this.tentacleHealth[t] <= 0) {
                this.playSound(SoundEvents.SCULK_BLOCK_BREAK, 1.0F, 1.0F);
            }

            return true;
        } else {
            return false;
        }
    }

    public void killTentacle(int index) {
        if (index < 0 || index >= TENTACLE_NUM) return;
        if (this.tentacleHealth[index] <= 0) return;
        this.tentacleHealth[index] = 0;
        this.syncTentacleData();
        this.playSound(SoundEvents.SCULK_BLOCK_BREAK, 1.0F, 1.0F);
    }

    // ========== Damage ==========

    @Override
    public boolean isPickable() {
        return false;
    }

    public float getDamageMultiplier() {
        float drPerTentacle = switch (this.level().getDifficulty()) {
            case NORMAL -> 0.30f;
            case HARD -> 0.35f;
            default -> 0.25f;
        };
        float multiplier = 1.0f;
        int alive = this.getAliveTentacleCount();
        for (int i = 0; i < alive; i++) {
            multiplier *= (1.0f - drPerTentacle);
        }
        return multiplier;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALLING_ANVIL)) {
            return false;
        }
        return super.hurt(source, amount * this.getDamageMultiplier());
    }

    // ========== Tick & Positioning ==========

    @Override
    public void tick() {
        this.body.tick();

        for (int t = 0; t < TENTACLE_NUM; t++) {
            for (int s = 0; s < SEGMENTS_PER_TENTACLE; s++) {
                this.stalkerSegments[t][s].tick();
            }
        }

        super.tick();
        this.updateTentaclePositions();

        if (this.level().isClientSide()) return;

        boolean dirty = false;
        for (int t = 0; t < TENTACLE_NUM; t++) {
            if (this.tentacleHurtTime[t] > 0) {
                this.tentacleHurtTime[t]--;
                dirty = true;
            }
        }
        if (dirty) this.syncTentacleHurtTimes();
        this.sculkConversionCooldown--;
        if (this.sculkConversionCooldown <= 0) {
            this.convertToSculk();
            this.sculkConversionCooldown = CONVERSION_COOLDOWN;
        }
    }

    /**
     * 计算触手段相对于实体中心的偏移量 (dx, dy, dz)，用于基础周期性运动。
     */
    public static Vec3 getTentacleOffset(int tentacleIdx, int segmentIdx, float tick) {
        return getTentacleOffset(tentacleIdx, segmentIdx, tick, null, 0, 0, 0);
    }

    /**
     * 扩展版本，添加行为参数以实现有机的章鱼式触手运动。
     * 包含波动传播、目标追踪、攻击突刺和受伤抽搐。
     *
     * @param targetDir  指向目标的世界空间方向向量，无目标时为 null
     * @param entityYaw  实体 yRot 角度（用于将目标方向转换到局部空间）
     * @param attackAnim 攻击动画进度 0..1（0 = 未攻击）
     * @param hurtAnim   剩余受伤 tick（0 = 未受伤）
     */
    public static Vec3 getTentacleOffset(
        int tentacleIdx,
        int segmentIdx,
        float tick,
        @Nullable Vec3 targetDir,
        float entityYaw,
        float attackAnim,
        float hurtAnim
    ) {
        double baseAngle = 2.0 * Math.PI * tentacleIdx / TENTACLE_NUM;
        float seed = tentacleIdx * 1.371F;
        float dist = 1.0F + segmentIdx * 0.55F;

        // === 波动传播：相位从基部(0)向尖端(4)累积延迟 ===
        // 产生沿触手向外传播的可见波动
        float wavePhase = segmentIdx * 0.6F;
        float waveFreq = 0.045F;
        float waveAmp = 0.15F + segmentIdx * 0.08F;
        float wave = (float) (Math.sin((tick + seed * 37) * waveFreq - wavePhase) * waveAmp);
        float wave2 = (float) (Math.sin((tick + seed * 53) * 0.025F - segmentIdx * 0.3F) * 0.15F);

        // === 目标追踪：触手朝向目标弯曲 ===
        float targetBend = 0;
        float alertLift = 0;
        if (targetDir != null && targetDir.lengthSqr() > 0.001) {
            float yawRad = entityYaw * ((float) Math.PI / 180F);
            double lx = targetDir.x * Math.cos(-yawRad) - targetDir.z * Math.sin(-yawRad);
            double lz = targetDir.x * Math.sin(-yawRad) + targetDir.z * Math.cos(-yawRad);
            float localTargetAngle = (float) Math.atan2(lx, lz);

            float angleDiff = (float) Math.atan2(
                Math.sin(localTargetAngle - baseAngle),
                Math.cos(localTargetAngle - baseAngle)
            );
            targetBend = angleDiff * 0.35F * Math.max(0, 1.0F - segmentIdx * 0.15F);
            alertLift = 0.20F * (1.0F - segmentIdx * 0.12F);
        }

        // === 攻击突刺：触手向前猛刺并回缩 ===
        float attackLunge = 0;
        if (attackAnim > 0 && attackAnim < 1.0F) {
            float phase = (float) Math.sin(attackAnim * (float) (Math.PI * 2));
            attackLunge = phase * 0.25F * (1.0F + segmentIdx * 0.2F);
        }

        // === 受伤抽搐：受到伤害时短暂回缩 ===
        float flinch = 0;
        if (hurtAnim > 0) {
            flinch = (float) (Math.sin(hurtAnim * Math.PI * 0.5) * 0.15F * (1.0F - segmentIdx * 0.1F));
        }

        // === 水平方向合成角 ===
        float effectiveAngle = (float) (
            baseAngle + targetBend
            + wave * 0.15F + wave2 * 0.1F
        );

        float sA = (float) Math.sin(effectiveAngle);
        float cA = (float) Math.cos(effectiveAngle);

        // === 垂直位置（重力下垂 + 自然浮动 + 警戒抬升 + 受伤抖动） ===
        float droop = -segmentIdx * 0.05F;
        float hBob = (float) (Math.sin((tick + seed * 23 + segmentIdx * 7) * 0.06F) * 0.2F);
        float hHurt = (hurtAnim > 0) ? (float) (Math.sin(hurtAnim * Math.PI * 0.3) * 0.1F) : 0;

        float hVar = droop + hBob + 0.1F + Math.max(0, alertLift) + hHurt;

        float totalDist = dist + attackLunge + flinch * 0.5F;

        return new Vec3(sA * totalDist, hVar, cA * totalDist);
    }

    private void updateTentaclePositions() {
        this.body.setPos(this.getX(), this.getY(), this.getZ());

        Vec3 targetDir = this.getTarget() != null
                         ? this.getTarget().position().subtract(this.position()) : null;
        float attackProgress = this.swinging
                               ? Math.max(0, this.swingTime / 10.0F) : 0;

        for (int tentacle_idx = 0; tentacle_idx < TENTACLE_NUM; tentacle_idx++) {
            for (int segment_idx = 0; segment_idx < SEGMENTS_PER_TENTACLE; segment_idx++) {
                StalkerSegment seg = this.stalkerSegments[tentacle_idx][segment_idx];
                if (!this.isTentacleAlive(tentacle_idx)) {
                    seg.setPos(MathUtil.copy(this.position()).add(new Vec3(0, -1, 0)));
                } else {
                    Vec3 offset = getTentacleOffset(
                        tentacle_idx, segment_idx, this.tickCount,
                        targetDir, this.getYRot(), attackProgress, this.tentacleHurtTime[tentacle_idx]
                    );
                    seg.setPos(MathUtil.copy(this.position()).add(offset));
                }
            }
        }
    }

    // ========== Sculk Conversion ==========

    protected void convertToSculk() {
        BlockPos pos = this.blockPosition();
        for (BlockPos offPos : CONVERT_RANGE) {
            BlockPos targetPos = pos.offset(offPos);
            BlockState state = this.level().getBlockState(targetPos);
            if (state.is(BlockTags.SCULK_REPLACEABLE)) {
                this.level().setBlockAndUpdate(targetPos, Blocks.SCULK.defaultBlockState());
            }
        }
    }

    // ========== Segment Data ==========

    public float getSegmentScale(int tentacle) {
        return this.isTentacleAlive(tentacle) ? 1.0F : 0.0F;
    }

    private void syncTentacleData() {
        byte bits = 0;
        for (int t = 0; t < TENTACLE_NUM; t++) {
            if (this.tentacleHealth[t] > 0) {
                bits |= (byte) (1 << t);
            }
        }
        this.entityData.set(ID_TENTACLES, bits);
    }

    private void syncTentacleHurtTimes() {
        int packed = 0;
        for (int t = 0; t < TENTACLE_NUM; t++) {
            packed |= (this.tentacleHurtTime[t] & 0xF) << (t * 4);
        }
        this.entityData.set(ID_TENTACLE_HURT_TIMES, packed);
    }

    public int getTentacleHurtTime(int tentacleIdx) {
        if (tentacleIdx < 0 || tentacleIdx >= TENTACLE_NUM) return 0;
        if (this.level().isClientSide()) {
            int packed = this.entityData.get(ID_TENTACLE_HURT_TIMES);
            return (packed >> (tentacleIdx * 4)) & 0xF;
        }
        return this.tentacleHurtTime[tentacleIdx];
    }

    // ========== Tentacle Query ==========

    public boolean isTentacleAlive(int index) {
        if (index >= TENTACLE_NUM || index < 0) return false;

        if (this.level().isClientSide()) {
            return (this.entityData.get(ID_TENTACLES) & (1 << index)) != 0;
        }
        return this.tentacleHealth[index] > 0;
    }

    public int getAliveTentacleCount() {
        if (this.level().isClientSide()) {
            return Integer.bitCount(this.entityData.get(ID_TENTACLES));
        }
        int count = 0;
        for (int t = 0; t < TENTACLE_NUM; t++) {
            if (this.tentacleHealth[t] > 0) count++;
        }
        return count;
    }

    // ========== Save / Load ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByteArray("TentacleHealth", this.tentacleHealth);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("TentacleHealth")) {
            byte[] data = tag.getByteArray("TentacleHealth");
            if (data.length == TENTACLE_NUM) {
                System.arraycopy(data, 0, this.tentacleHealth, 0, TENTACLE_NUM);
            } else {
                Arrays.fill(this.tentacleHealth, (byte) getTentacleMaxHealth());
            }
        } else {
            Arrays.fill(this.tentacleHealth, (byte) getTentacleMaxHealth());
        }
        this.syncTentacleData();
    }

    private float getTentacleMaxHealth() {
        return this.getMaxHealth() * TENTACLE_HEALTH_RATIO;
    }
}
