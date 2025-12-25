package dev.anvilcraft.pigsplus.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ChiseledBookShelfUtil {
    /**
     * 统计这个区域内的所有雕纹书架中有多少附魔等级
     */
    public static int countEnchantmentLevelsInArea(Level level, BlockPos blockPos, int distance) {
        int count = 0;
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                for (int k = -distance; k <= distance; k++) {
                    mpos.set(blockPos).move(i, j, k);
                    count += countEnchantmentLevelsInBlock(level, mpos);
                }
            }
        }
        return count;
    }

    /**
     * 统计这个雕纹书架中有多少附魔等级
     */
    public static int countEnchantmentLevelsInBlock(Level level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)) return 0;

        BlockState blockState = level.getBlockState(pos);
        if (!(blockState.getBlock() instanceof ChiseledBookShelfBlock)) return 0;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChiseledBookShelfBlockEntity shelfEntity)) return 0;

        int count = 0;
        for (int i = 0; i < 6; i++) {
            count += countEnchantmentLevelsInItem(shelfEntity.getItem(i));
        }
        return count;
    }

    /**
     * 统计这个附魔书中有多少附魔等级
     */
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

    /**
     * 统计这个区域内的所有雕纹书架中有哪些附魔
     */
    public static List<Object2IntMap.Entry<Holder<Enchantment>>> countEnchantmentsInArea(
        Level level,
        BlockPos blockPos,
        List<BlockPos> positions
    ) {
        List<Object2IntMap.Entry<Holder<Enchantment>>> enchantments = new ArrayList<>();
        for (BlockPos pos : positions) {
            enchantments.addAll(countEnchantmentsInBlock(level, blockPos.offset(pos)));
        }
        return enchantments;
    }

    /**
     * 统计这个雕纹书架中有哪些附魔
     */
    public static List<Object2IntMap.Entry<Holder<Enchantment>>> countEnchantmentsInBlock(Level level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)) return new ArrayList<>();

        BlockState blockState = level.getBlockState(pos);
        if (!(blockState.getBlock() instanceof ChiseledBookShelfBlock)) return new ArrayList<>();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChiseledBookShelfBlockEntity shelfEntity)) return new ArrayList<>();

        List<Object2IntMap.Entry<Holder<Enchantment>>> enchantments = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            enchantments.addAll(countEnchantmentsInItem(shelfEntity.getItem(i)));
        }
        return enchantments;
    }

    /**
     * 统计这个附魔书中有哪些附魔
     */
    public static List<Object2IntMap.Entry<Holder<Enchantment>>> countEnchantmentsInItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) return new ArrayList<>();

        ItemEnchantments itemEnchantments = itemStack.get(DataComponents.STORED_ENCHANTMENTS);
        if (itemEnchantments == null || itemEnchantments.isEmpty()) return new ArrayList<>();

        return itemEnchantments.entrySet().stream().toList();

    }
}
