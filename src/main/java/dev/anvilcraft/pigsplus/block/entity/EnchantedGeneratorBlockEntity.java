package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.EnchantedGeneratorBlock;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.anvilcraft.pigsplus.util.ChiseledBookShelfUtil;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.api.tooltip.providers.IHasAffectRange;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.CONFIG;

public class EnchantedGeneratorBlockEntity extends BlockEntity implements IPowerProducer, IHasAffectRange {
    public static final int COOLDOWN = 2;
    public static final float ROTATION_PRE_POWER = 0.002f;
    private int cooldownCount = 2;
    private int consumedCount = 0;
    private int top_power = 0;
    private int power = 0;
    private int oldPowerAbility = 0;
    @Setter
    @Getter
    private @Nullable PowerGrid grid;
    @Getter
    private int time = 0;
    @Getter
    private float rotation = 0;

    public static final List<BlockPos> SELECT_RANGE =
        BlockPos.betweenClosedStream(-1, 1, -1, 1, 1, 1).toList();

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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("cooldownCount", this.cooldownCount);
        output.putInt("power", this.power);
        output.putInt("oldPowerAbility", this.oldPowerAbility);
        output.putInt("top_power", this.top_power);
        output.putInt("consumedCount", this.consumedCount);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.getIntOr("cooldownCount", 2);
        input.getIntOr("power", 0);
        input.getIntOr("oldPowerAbility", 0);
        input.getIntOr("top_power", 0);
        input.getIntOr("consumedCount", 0);
    }

    @Override
    public void gridTick() {
        if (level == null || level.isClientSide()) return;
        if (this.cooldownCount-- > 1) return;
        this.cooldownCount = COOLDOWN;
        int prePower = power;
        if (!isAnotherCollectorNearby(level, getBlockPos())) {
            int enchantmentLevel = ChiseledBookShelfUtil.countEnchantmentLevelsInArea(
                level,
                getBlockPos(),
                SELECT_RANGE
            );
            int commonPowerAbility = enchantmentLevel * CONFIG.enchantedGenerator.powerPerLevel;
            if (oldPowerAbility >= CONFIG.enchantedGenerator.maxCommonPower + 100) {
                // 进入超频状态
                int overclockingPowerAbility = commonPowerAbility * CONFIG.enchantedGenerator.overclockingAmplification;
                oldPowerAbility = overclockingPowerAbility;
                power = Math.min(overclockingPowerAbility, CONFIG.enchantedGenerator.maxOverclockingPower);
                top_power = Math.max(top_power, power);
                tryConsumeBook();
            } else {
                oldPowerAbility = commonPowerAbility;
                power = Math.min(commonPowerAbility, CONFIG.enchantedGenerator.maxCommonPower);
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

        consumedCount = Math.round((float) CONFIG.enchantedGenerator.maxOverclockingPower * CONFIG.enchantedGenerator.minConsumeCooldown / top_power);
        top_power = 0;
        // 寻找有附魔书的所有书架
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        List<BlockPos> BookShelfBlocksPos = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    mpos.set(this.getBlockPos()).move(i, j, k);
                    if (ChiseledBookShelfUtil.countEnchantmentLevelsInBlock(level, mpos) != 0) BookShelfBlocksPos.add(mpos.immutable());
                }
            }
        }

        // 随机选择一个书架
        if (BookShelfBlocksPos.isEmpty()) return;
        BlockPos BookShelfBlockPos = BookShelfBlocksPos.get(level.getRandom().nextInt(BookShelfBlocksPos.size()));
        if (level.getBlockEntity(BookShelfBlockPos) instanceof ChiseledBookShelfBlockEntity shelfEntity) {
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                ItemStack itemStack = shelfEntity.getItem(i);
                if (ChiseledBookShelfUtil.countEnchantmentLevelsInItem(itemStack) == 0) continue;
                indexes.add(i);
            }
            // 随机选择一本书，删除它
            if (indexes.isEmpty()) return;
            int idx = indexes.get(level.getRandom().nextInt(indexes.size()));
            shelfEntity.removeItem(idx, 1);

            level.playSound(
                null,
                getBlockPos(),
                SoundEvents.PLAYER_BURP,
                SoundSource.BLOCKS,
                0.3F,
                level.getRandom().nextFloat() * 0.1F + 1.1F
            );
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
