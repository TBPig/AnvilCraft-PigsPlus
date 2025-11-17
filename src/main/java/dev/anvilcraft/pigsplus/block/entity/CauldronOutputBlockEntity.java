package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.CauldronOutputBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CauldronOutputBlockEntity extends BlockEntity {
    public CauldronOutputBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void tick() {
        if (level == null) return;

        BlockState state = getBlockState();
        if (!state.is(AddonBlocks.CAULDRON_OUTPUT.get())) return;

        Direction facing = state.getValue(CauldronOutputBlock.FACING);
        BlockPos cauldronPos = getBlockPos().relative(facing.getOpposite());
        BlockState cauldronState = level.getBlockState(cauldronPos);
        if (!cauldronState.is(BlockTags.CAULDRONS)) return;

        AABB searchBox = new AABB(cauldronPos);
        level.getEntities(EntityType.ITEM, searchBox, (e)->!e.anvilcraft$isAdsorbable())
            .forEach(entity -> {
                Vec3 targetPos = getBlockPos().getCenter().add(
                    -facing.getStepX() * 0.2,
                    0,
                    -facing.getStepZ() * 0.2
                );
                entity.teleportTo(targetPos.x, targetPos.y, targetPos.z);


                entity.setDeltaMovement(Vec3.ZERO);
            });
    }
}