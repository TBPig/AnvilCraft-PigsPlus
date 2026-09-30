package dev.anvilcraft.pigsplus.block.entity;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SoulboundCampfireBlockEntity extends BlockEntity implements Clearable {
    // 激活相关
    public static final String AGE_PROPERTY_NAME = "age";
    public static final String WORK_TICKS_TAG = "WorkTicks";
    public static final int MAX_WORK_TICKS = 100;
    public static final int EXTRA_WORK_TICKS = 4;
    public static final int CROP_HORIZONTAL_RANGE = 1;
    private int workTicks = 0;

    // 烹饪相关
    public static final String COOKING_TIMES_TAG = "CookingTimes";
    public static final String COOKING_TOTAL_TIMES_TAG = "CookingTotalTimes";
    public static final int BURN_SPEED = 4;
    public static final int BURN_COOL_SPEED = 2;
    public static final int NUM_SLOTS = 4;
    @Getter
    private final NonNullList<ItemStack> items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[NUM_SLOTS];
    private final int[] cookingTime = new int[NUM_SLOTS];
    private final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck =
        RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    // 效果相关
    public static final double AURA_RADIUS = 4.0;

    public SoulboundCampfireBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public static void cookTick(Level level, BlockPos pos, BlockState state, SoulboundCampfireBlockEntity campfire) {
        boolean changed = false;
        for (int i = 0; i < campfire.items.size(); i++) {
            ItemStack stack = campfire.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            changed = true;
            campfire.cookingProgress[i] += BURN_SPEED;
            if (campfire.cookingProgress[i] < campfire.cookingTime[i]) {
                continue;
            }
            SingleRecipeInput input = new SingleRecipeInput(stack);
            ItemStack result = campfire.quickCheck
                .getRecipeFor(input, level)
                .map(holder -> holder.value().assemble(input, level.registryAccess()))
                .orElse(stack);
            if (!result.isItemEnabled(level.enabledFeatures())) {
                continue;
            }
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
            campfire.items.set(i, ItemStack.EMPTY);
            level.sendBlockUpdated(pos, state, state, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
        }
        if (changed) {
            setChanged(level, pos, state);
        }
    }

    public static void cooldownTick(Level level, BlockPos pos, BlockState state, SoulboundCampfireBlockEntity campfire) {
        boolean changed = false;
        for (int i = 0; i < campfire.items.size(); i++) {
            if (campfire.cookingProgress[i] <= 0) {
                continue;
            }
            changed = true;
            campfire.cookingProgress[i] = Mth.clamp(
                campfire.cookingProgress[i] - BURN_COOL_SPEED,
                0,
                campfire.cookingTime[i]
            );
        }
        if (changed) {
            setChanged(level, pos, state);
        }
    }

    public static void particleTick(Level level, BlockPos pos, BlockState state, SoulboundCampfireBlockEntity campfire) {
        if (!state.getValue(CampfireBlock.LIT)) {
            return;
        }
        RandomSource random = level.random;
        if (random.nextFloat() < 0.11F) {
            for (int i = 0; i < random.nextInt(2) + 2; i++) {
                CampfireBlock.makeParticles(level, pos, state.getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        int facingOffset = state.getValue(CampfireBlock.FACING).get2DDataValue();
        for (int i = 0; i < campfire.items.size(); i++) {
            if (campfire.items.get(i).isEmpty() || random.nextFloat() >= 0.2F) {
                continue;
            }
            Direction direction = Direction.from2DDataValue(Math.floorMod(i + facingOffset, 4));
            double x = pos.getX() + 0.5 - direction.getStepX() * 0.3125 + direction.getClockWise().getStepX() * 0.3125;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 - direction.getStepZ() * 0.3125 + direction.getClockWise().getStepZ() * 0.3125;
            for (int k = 0; k < 4; k++) {
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    /**
     * 推进缠魂营火的燃烧与工作循环
     *
     * <p>工作时长即燃烧时长，逐刻递减：耗尽时（小于等于 1gt）才一次性蚕食周围 8 格内的所有作物，
     * 每株为其补充 4gt 燃烧时长
     * 因此玩家必须持续供给作物才能维持其燃烧</p>
     */
    public static void soulTick(ServerLevel level, BlockPos pos, BlockState state, SoulboundCampfireBlockEntity campfire) {
        campfire.workTicks--;
        if (campfire.workTicks >= 1) return;

        campfire.workTicks += consumeCrops(level, pos) * EXTRA_WORK_TICKS;
        if (campfire.workTicks <= 0) {
            extinguish(level, pos, state);
            campfire.workTicks = 0;
            return;
        }
        campfire.workTicks = Math.min(MAX_WORK_TICKS, campfire.workTicks);
    }

    /**
     * 燃烧耗尽且周围已无作物可蚕食时熄灭营火
     */
    private static void extinguish(ServerLevel level, BlockPos pos, BlockState state) {
        level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
    }

    /**
     * 工作时为周围玩家提供生命恢复III
     */
    public static void auraTick(ServerLevel level, BlockPos pos, SoulboundCampfireBlockEntity campfire) {
        if (!campfire.isWorking()) {
            return;
        }
        if (level.getGameTime() % 100 != 0) {
            return;
        }
        List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(AURA_RADIUS));
        for (Player player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, true, true));
            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 200, 4, true, true));
        }
    }

    public boolean isWorking() {
        return this.workTicks > 0;
    }

    /**
     * 一次性蚕食周围 8 格内的所有作物
     *
     * @return 成功蚕食的作物数量
     */
    private static int consumeCrops(ServerLevel level, BlockPos pos) {
        int consumed = 0;
        for (int dx = -CROP_HORIZONTAL_RANGE; dx <= CROP_HORIZONTAL_RANGE; dx++) {
            for (int dz = -CROP_HORIZONTAL_RANGE; dz <= CROP_HORIZONTAL_RANGE; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                if (regressCrop(level, pos.offset(dx, 0, dz))) {
                    consumed++;
                }
            }
        }
        return consumed;
    }

    /**
     * 使作物降低一点生长层数或消失
     *
     * @return 是否成功蚕食
     */
    private static boolean regressCrop(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.CROPS)) {
            return false;
        }
        IntegerProperty age = findAgeProperty(state);
        if (age == null || state.getValue(age) <= 0) {
            return false;
        }
        level.setBlockAndUpdate(pos, state.setValue(age, state.getValue(age) - 1));
        level.sendParticles(
            ParticleTypes.SOUL,
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            3,
            0.3,
            0.4,
            0.3,
            0.01
        );
        return true;
    }

    @Nullable
    private static IntegerProperty findAgeProperty(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty ageProperty && AGE_PROPERTY_NAME.equals(property.getName())) {
                return ageProperty;
            }
        }
        return null;
    }

    public int getRemainingCookTicks(int slot) {
        int remaining = this.cookingTime[slot] - this.cookingProgress[slot];
        if (remaining <= 0) {
            return 0;
        }
        return Mth.ceil(remaining / (float) BURN_SPEED);
    }

    public Optional<RecipeHolder<CampfireCookingRecipe>> getCookableRecipe(ItemStack stack) {
        Level level = this.getLevel();
        if (level == null || this.items.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return this.quickCheck.getRecipeFor(new SingleRecipeInput(stack), level);
    }

    public boolean placeFood(@Nullable LivingEntity entity, ItemStack food, int cookTime) {
        Level level = this.getLevel();
        if (level == null) {
            return false;
        }
        for (int i = 0; i < this.items.size(); i++) {
            if (!this.items.get(i).isEmpty()) {
                continue;
            }
            this.cookingTime[i] = cookTime;
            this.cookingProgress[i] = 0;
            this.items.set(i, food.consumeAndReturn(1, entity));
            level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
            this.markUpdated();
            return true;
        }
        return false;
    }

    private void markUpdated() {
        Level level = this.getLevel();
        if (level == null) {
            return;
        }
        this.setChanged();
        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items, registries);
        if (tag.contains(COOKING_TIMES_TAG, 11)) {
            int[] progress = tag.getIntArray(COOKING_TIMES_TAG);
            System.arraycopy(progress, 0, this.cookingProgress, 0, Math.min(this.cookingProgress.length, progress.length));
        }
        if (tag.contains(COOKING_TOTAL_TIMES_TAG, 11)) {
            int[] total = tag.getIntArray(COOKING_TOTAL_TIMES_TAG);
            System.arraycopy(total, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, total.length));
        }
        this.workTicks = tag.getInt(WORK_TICKS_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, true, registries);
        tag.putIntArray(COOKING_TIMES_TAG, this.cookingProgress);
        tag.putIntArray(COOKING_TOTAL_TIMES_TAG, this.cookingTime);
        tag.putInt(WORK_TICKS_TAG, this.workTicks);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, this.items, true, registries);
        return tag;
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }
}
