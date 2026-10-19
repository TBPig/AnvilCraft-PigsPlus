package dev.anvilcraft.pigsplus.block.handler;

import dev.anvilcraft.pigsplus.block.entity.WirelessTransmitterBlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * 无线传输器流体侧的存储能力：只进不出。
 *
 * <p>只在底座（贴着来源容器的一面）暴露。填充的流体立即尝试送往绑定的目标方块，
 * 等价于向目标方块输入；该路径独立于主动传输，不消耗电力也没有冷却。</p>
 */
public class WirelessTransmitterFluidHandler implements IFluidHandler {
    private final WirelessTransmitterBlockEntity transmitter;

    public WirelessTransmitterFluidHandler(WirelessTransmitterBlockEntity transmitter) {
        this.transmitter = transmitter;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return this.transmitter.sendToTarget(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }
}
