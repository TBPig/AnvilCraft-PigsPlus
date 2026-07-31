package dev.anvilcraft.pigsplus.block.handler;

import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class BrassSinkFluidHandler implements IFluidHandler {
    public static final BrassSinkFluidHandler INSTANCE = new BrassSinkFluidHandler();

    private BrassSinkFluidHandler() {
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank == 0 ? new FluidStack(Fluids.WATER, Integer.MAX_VALUE) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? Integer.MAX_VALUE : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tank == 0 && stack.getFluid() == Fluids.WATER;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return isFluidValid(0, resource) ? resource.getAmount() : 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return isFluidValid(0, resource)
            ? new FluidStack(Fluids.WATER, resource.getAmount())
            : FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return maxDrain > 0 ? new FluidStack(Fluids.WATER, maxDrain) : FluidStack.EMPTY;
    }
}
