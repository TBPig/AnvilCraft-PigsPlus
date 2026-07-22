package dev.anvilcraft.pigsplus.util;

import dev.dubhe.anvilcraft.init.block.ModFluids;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidUtil {
    public static int fill(IFluidHandler handler, TagKey<Fluid> fluidTagKey, int liquidExp, IFluidHandler.FluidAction action) {
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            Holder<Fluid> tankFluid = handler.getFluidInTank(tank).getFluidHolder();
            if (tankFluid.is(fluidTagKey)) {
                int accepted = handler.fill(new FluidStack(tankFluid, liquidExp), action);
                if (accepted > 0) return accepted;
            }
        }
        return handler.fill(new FluidStack(ModFluids.EXP_FLUID, liquidExp), action);
    }

    public static FluidStack drain(IFluidHandler handler, TagKey<Fluid> fluidTagKey, int liquidExp, IFluidHandler.FluidAction action) {
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            Holder<Fluid> tankFluid = handler.getFluidInTank(tank).getFluidHolder();
            if (tankFluid.is(fluidTagKey)) {
                FluidStack accepted = handler.drain(new FluidStack(tankFluid, liquidExp), action);
                if (accepted.getAmount() > 0) return accepted;
            }
        }
        return FluidStack.EMPTY;
    }
}
