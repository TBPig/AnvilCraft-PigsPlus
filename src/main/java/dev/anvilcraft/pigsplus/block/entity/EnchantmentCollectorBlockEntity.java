package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.EnchantmentCollectorBlock;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.api.tooltip.providers.IHasAffectRange;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnchantmentCollectorBlockEntity extends BlockEntity implements IPowerProducer, IHasAffectRange {
    public static final int MAX_POWER = 4096;
    public static final int ENCHANTMENT_LEVELS_PER_COLLECTOR = 1;
    private static final int COOLDOWN = 2;
    private int cooldownCount = 2;
    private int power = 0;
    @Setter
    @Getter
    private PowerGrid grid = null;
    @Getter
    private int time = 0;
    @Getter
    private float rotation = 0;

    public EnchantmentCollectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public EnchantmentCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(AddonBlockEntities.ENCHANTMENT_COLLECTOR.get(), pos, state);
    }

    public static EnchantmentCollectorBlockEntity createBlockEntity(
        BlockEntityType<?> type,
        BlockPos pos,
        BlockState blockState
    ) {
        return new EnchantmentCollectorBlockEntity(type, pos, blockState);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.cooldownCount = tag.getInt("cooldownCount");
        this.power = tag.getInt("power");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        tag.putInt("cooldownCount", this.cooldownCount);
        tag.putInt("power", this.power);
    }

    @Override
    public void gridTick() {
        if (level == null || level.isClientSide()) return;
        if (this.cooldownCount-- > 1) return;
        this.cooldownCount = COOLDOWN;
        int oldPower = this.power;
        int enchantment_levels = countEnchantmentLevels();
        this.power = Math.min(enchantment_levels * ENCHANTMENT_LEVELS_PER_COLLECTOR, MAX_POWER);
        if (this.power > 0 && this.getBlockState().getBlock() instanceof EnchantmentCollectorBlock enchantmentCollectorBlock) {
            enchantmentCollectorBlock.activate(this.level, this.getBlockPos(), this.getBlockState());
        }
        if (power != oldPower && grid != null) grid.markChanged();
        time++;
    }

    private int countEnchantmentLevels() {
        if (level == null || level.isClientSide()) return 0;
        if (isAnotherCollectorNearby(level, getBlockPos())) return 0;
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    mpos.set(this.getBlockPos()).move(i, j, k);
                    count += countEnchantmentLevelsInBlock(level, mpos);
                }
            }
        }
        return count;
    }

    public static int countEnchantmentLevelsInBlock(Level level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)) return 0;

        BlockState blockState = level.getBlockState(pos);
        if (!(blockState.getBlock() instanceof ChiseledBookShelfBlock)) return 0;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChiseledBookShelfBlockEntity shelfEntity) {
            int count = 0;
            for (int i = 0; i < 6; i++) {
                count += countEnchantmentLevelsInItem(shelfEntity.getItem(i));
            }
            return count;
        }
        return 0;
    }

    public static int countEnchantmentLevelsInItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) return 0;

        ItemEnchantments itemEnchantments = itemStack.get(DataComponents.STORED_ENCHANTMENTS);
        if (itemEnchantments == null || itemEnchantments.isEmpty()) return 0;

        ItemEnchantments.Mutable storedEnchantmentsMutable = new ItemEnchantments.Mutable(itemEnchantments);
        int count = 0;
        for (Holder<Enchantment> enchantment : storedEnchantmentsMutable.keySet()) {
            count += storedEnchantmentsMutable.getLevel(enchantment);
        }
        return count;
    }


    public static boolean isAnotherCollectorNearby(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 2; k++) {
                    mpos.set(pos).move(i, j, k);
                    if (level.isOutsideBuildHeight(mpos)) continue;
                    BlockState blockState = level.getBlockState(mpos);
                    if (blockState.getBlock() instanceof EnchantmentCollectorBlock && (i != 0 || j != 0 || k != 0)) {
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
        rotation += (float) (getServerPower() * 0.03);
    }
}
