package dev.anvilcraft.pigsplus.block.handler;

import dev.anvilcraft.pigsplus.block.entity.WirelessTransmitterBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 无线传输器物品侧的存储能力：只进不出。
 *
 * <p>只在底座（贴着来源容器的一面）暴露。插入的物品立即尝试送往绑定的目标方块，
 * 等价于向目标方块输入；该路径独立于主动传输，不消耗电力也没有冷却。</p>
 */
public class WirelessTransmitterItemHandler implements IItemHandler {
    private final WirelessTransmitterBlockEntity transmitter;

    public WirelessTransmitterItemHandler(WirelessTransmitterBlockEntity transmitter) {
        this.transmitter = transmitter;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return this.transmitter.sendToTarget(stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }
}
