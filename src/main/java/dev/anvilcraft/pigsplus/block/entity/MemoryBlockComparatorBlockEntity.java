package dev.anvilcraft.pigsplus.block.entity;

import dev.dubhe.anvilcraft.api.item.IDiskCloneable;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MemoryBlockComparatorBlockEntity extends BlockEntity implements IDiskCloneable {
    @Getter
    private BlockState rememberedState = Blocks.AIR.defaultBlockState();

    public MemoryBlockComparatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setRememberedState(BlockState state) {
        this.rememberedState = state;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("remembered_state", NbtUtils.writeBlockState(this.rememberedState));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("remembered_state")) {
            this.rememberedState = NbtUtils.readBlockState(
                registries.lookupOrThrow(Registries.BLOCK),
                tag.getCompound("remembered_state")
            );
        }
    }

    @Override
    public void storeDiskData(CompoundTag tag) {
        tag.put("remembered_state", NbtUtils.writeBlockState(this.rememberedState));
    }

    @Override
    public void applyDiskData(CompoundTag tag) {
        if (tag.contains("remembered_state")) {
            this.rememberedState = NbtUtils.readBlockState(
                BuiltInRegistries.BLOCK.asLookup(),
                tag.getCompound("remembered_state")
            );
        }
        setChanged();
        if (level != null && !level.isClientSide) {
            level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 2);
        }
    }
}
