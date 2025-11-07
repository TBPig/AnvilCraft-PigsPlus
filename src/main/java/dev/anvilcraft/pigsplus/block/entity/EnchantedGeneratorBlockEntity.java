package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.EnchantedGeneratorBlock;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.api.tooltip.providers.IHasAffectRange;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.anvilcraft.pigsplus.util.ChiseledBookShelfUtil.countEnchantmentLevelsInArea;
import static dev.anvilcraft.pigsplus.util.ChiseledBookShelfUtil.countEnchantmentLevelsInBlock;
import static dev.anvilcraft.pigsplus.util.ChiseledBookShelfUtil.countEnchantmentLevelsInItem;

public class EnchantedGeneratorBlockEntity extends BlockEntity implements IPowerProducer, IHasAffectRange {
    public static final int MAX_OVERCLOCKING_POWER = 16384;
    public static final int MAX_COMMON_POWER = 1024;
    public static final int OVERCLOCKING_POWER = MAX_COMMON_POWER + 100;
    public static final int POWER_PER_LEVEL = 2;
    public static final int OVERCLOCKING_POWER_MULTIPLE = 2;
    public static final int MIN_CONSUME_COOLDOWN = 30;
    public static final int COOLDOWN = 2;
    public static final float ROTATION_PRE_POWER = 0.002f;
    private int cooldownCount = 2;
    private int consumedCount = 0;
    private int top_power = 0;
    private int power = 0;
    private int oldPowerAbility = 0;
    @Setter
    @Getter
    private PowerGrid grid = null;
    @Getter
    private int time = 0;
    @Getter
    private float rotation = 0;

    public EnchantedGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public EnchantedGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AddonBlockEntities.ENCHANTMENT_GENERATOR.get(), pos, state);
    }

    public static EnchantedGeneratorBlockEntity createBlockEntity(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState blockState
    ) {
        return new EnchantedGeneratorBlockEntity(type, pos, blockState);
    }

    @Override
    public void loadAdditional(  CompoundTag tag, HolderLookup.  Provider registries) {
        super.loadAdditional(tag, registries);
        this.cooldownCount = tag.getInt("cooldownCount");
        this.power = tag.getInt("power");
        this.oldPowerAbility = tag.getInt("oldPowerAbility");
        this.top_power = tag.getInt("top_power");
        this.consumedCount = tag.getInt("consumedCount");
    }

    @Override
    public void saveAdditional(  CompoundTag tag, HolderLookup.  Provider registries) {
        super.loadAdditional(tag, registries);
        tag.putInt("cooldownCount", this.cooldownCount);
        tag.putInt("power", this.power);
        tag.putInt("oldPowerAbility", this.oldPowerAbility);
        tag.putInt("top_power", this.top_power);
        tag.putInt("consumedCount", this.consumedCount);
    }

    @Override
    public void gridTick() {
        if (level == null || level.isClientSide()) return;
        if (this.cooldownCount-- > 1) return;
        this.cooldownCount = COOLDOWN;
        int prePower = power;
        if (!isAnotherCollectorNearby(level, getBlockPos())) {
            int commonPowerAbility = countEnchantmentLevelsInArea(level, getBlockPos(), 1) * POWER_PER_LEVEL;
            if (oldPowerAbility >= OVERCLOCKING_POWER) {
                // 进入超频状态
                int overclockingPowerAbility = commonPowerAbility * OVERCLOCKING_POWER_MULTIPLE;
                oldPowerAbility = overclockingPowerAbility;
                power = Math.min(overclockingPowerAbility, MAX_OVERCLOCKING_POWER);
                top_power = Math.max(top_power, power);
                tryConsumeBook();
            } else {
                oldPowerAbility = commonPowerAbility;
                power = Math.min(commonPowerAbility, MAX_COMMON_POWER);
            }
        } else {
            power = 0;
        }

        if (power > 0 && this.getBlockState().getBlock() instanceof EnchantedGeneratorBlock enchantedGeneratorBlock) {
            enchantedGeneratorBlock.activate(this.level, this.getBlockPos(), this.getBlockState());
        }
        if (power != prePower && grid != null) grid.markChanged();
        time++;
    }


    private void tryConsumeBook() {
        if (level == null || level.isClientSide()) return;
        if (consumedCount-- > 0) return;

        consumedCount = Math.round((float) MAX_OVERCLOCKING_POWER / top_power * MIN_CONSUME_COOLDOWN);
        top_power = 0;
        // 寻找有附魔书的所有书架
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        List<BlockPos> BookShelfBlocksPos = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    mpos.set(this.getBlockPos()).move(i, j, k);
                    if (countEnchantmentLevelsInBlock(level, mpos) != 0) BookShelfBlocksPos.add(mpos.immutable());
                }
            }
        }

        // 随机选择一个书架
        if (BookShelfBlocksPos.isEmpty()) return;
        BlockPos BookShelfBlockPos = BookShelfBlocksPos.get(level.random.nextInt(BookShelfBlocksPos.size()));
        if (level.getBlockEntity(BookShelfBlockPos) instanceof ChiseledBookShelfBlockEntity shelfEntity) {
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                ItemStack itemStack = shelfEntity.getItem(i);
                if (countEnchantmentLevelsInItem(itemStack) == 0) continue;
                indexes.add(i);
            }
            // 随机选择一本书，删除它
            if (indexes.isEmpty()) return;
            int idx = indexes.get(level.random.nextInt(indexes.size()));
            shelfEntity.removeItem(idx, 1);
        }
    }

    public static boolean isAnotherCollectorNearby(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 2; k++) {
                    mpos.set(pos).move(i, j, k);
                    if (level.isOutsideBuildHeight(mpos)) continue;
                    BlockState blockState = level.getBlockState(mpos);
                    if (blockState.getBlock() instanceof EnchantedGeneratorBlock && (i != 0 || j != 0 || k != 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return level;
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public int getOutputPower() {
        return power;
    }

    @Override
    public int getRange() {
        return 1;
    }

    @Override
    public AABB shape() {
        return AABB.ofSize(getBlockPos().getCenter(), 3, 3, 3);
    }

    public void clientTick() {
        rotation += getServerPower() * ROTATION_PRE_POWER;
    }
}
