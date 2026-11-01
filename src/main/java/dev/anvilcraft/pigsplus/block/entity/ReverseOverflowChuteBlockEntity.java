package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.block.OverflowChuteBlock;
import dev.dubhe.anvilcraft.block.entity.OverflowChuteBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ReverseOverflowChuteBlockEntity extends OverflowChuteBlockEntity {
    private boolean overflowAttemptedThisTick;
    private boolean overflowSucceededThisTick;

    public ReverseOverflowChuteBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected boolean validateBlockState(BlockState state) {
        return state.is(AddonBlocks.REVERSE_OVERFLOW_CHUTE.get());
    }

    @Override
    public void tick() {
        this.overflowAttemptedThisTick = false;
        this.overflowSucceededThisTick = false;
        // 有红石时侧出口关闭，只提前尝试无红石状态下的侧向输出
        if (this.getCooldown() <= 1 && !this.isRedstonePowered()) {
            this.tryOverflowOutput();
        }
        super.tick();
    }

    @Override
    protected boolean tryOverflowOutput() {
        if (this.isRedstonePowered()) {
            return false;
        }
        // 避免super.tick()再次进行侧向输出
        if (!this.overflowAttemptedThisTick) {
            this.overflowAttemptedThisTick = true;
            this.overflowSucceededThisTick = super.tryOverflowOutput();
        }
        return this.overflowSucceededThisTick;
    }

    @Override
    protected boolean isEnabled() {
        if (this.isRedstonePowered()) {
            return true;
        }
        // 如果侧输出口输出成功，阻止主输出口
        return !this.overflowSucceededThisTick && super.isEnabled();
    }

    private boolean isRedstonePowered() {
        return !this.getBlockState().getValue(OverflowChuteBlock.ENABLED);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.anvilcraft_pigsplus.reverse_overflow_chute");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }
}
