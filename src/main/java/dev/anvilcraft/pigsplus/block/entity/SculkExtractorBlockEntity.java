package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicInteger;

public class SculkExtractorBlockEntity extends BlockEntity {
    private int cooldown = 0;
    private static final int SCAN_COOLDOWN = 100;

    public SculkExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        cooldown = tag.getInt("Cooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("Cooldown", cooldown);
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;
        if (cooldown-- > 0) return;
        cooldown = SCAN_COOLDOWN;

        // 计算周围的幽匿类方块数量
        AtomicInteger sculkValue = new AtomicInteger();
        BlockPos.breadthFirstTraversal(
            getBlockPos(), 8, (int) Math.pow(2, 10),
            (blockPos, consumer) -> {
                for (Direction direction : Direction.values()) {
                    consumer.accept(blockPos.relative(direction));
                }
            },
            blockPos -> {
                BlockState state = level.getBlockState(blockPos);
                if (state.is(Blocks.SCULK) ||
                    state.is(Blocks.SCULK_VEIN) ||
                    state.is(Blocks.SCULK_CATALYST) ||
                    state.is(Blocks.SCULK_SHRIEKER) ||
                    state.is(Blocks.SCULK_SENSOR)) {
                    level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                    sculkValue.getAndIncrement();
                    return true;
                } else if (
                    state.is(AddonBlocks.ECHO_CLUSTER)) {
                    sculkValue.getAndAdd(50);
                    return true;
                } else if (state.is(AddonBlocks.BUDDING_ECHO_SHARD)) {
                    return true;
                } else {
                    return state.is(AddonBlocks.SCULK_EXTRACTOR);
                }
            }
        );

        // 根据周围的幽匿方块数量确定经验球的数量
        int experienceAmount = Math.max(0, 10 * sculkValue.get());
        if (experienceAmount == 0) return;
        Vec3 spawnPos = Vec3.atCenterOf(getBlockPos()).add(0, 1, 0);
        ExperienceOrb.award((ServerLevel) level, spawnPos, experienceAmount);
    }
}