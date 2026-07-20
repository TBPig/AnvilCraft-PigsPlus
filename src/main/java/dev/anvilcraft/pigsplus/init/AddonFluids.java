package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.fluid.VoidAcidFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AddonFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
        NeoForgeRegistries.FLUID_TYPES, AnvilCraftPigsPlus.MOD_ID
    );
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, AnvilCraftPigsPlus.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> VOID_ACID_TYPE = FLUID_TYPES.register(
        "void_acid",
        () -> new FluidType(FluidType.Properties.create()
            .descriptionId("block.anvilcraft_pigsplus.void_acid")
            .density(1200)
            .viscosity(400)
            .fallDistanceModifier(0)
            .motionScale(0.01)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        )
    );

    public static final DeferredHolder<Fluid, VoidAcidFluid> VOID_ACID = FLUIDS.register(
        "void_acid",
        () -> new VoidAcidFluid.Source(AddonFluids.VOID_ACID_PROPERTIES)
    );

    public static final DeferredHolder<Fluid, VoidAcidFluid> FLOWING_VOID_ACID = FLUIDS.register(
        "flowing_void_acid",
        () -> new VoidAcidFluid.Flowing(AddonFluids.VOID_ACID_PROPERTIES)
    );

    public static final VoidAcidFluid.Properties VOID_ACID_PROPERTIES = new VoidAcidFluid.Properties(
        VOID_ACID_TYPE, VOID_ACID, FLOWING_VOID_ACID
    )
        .bucket(AddonItems.VOID_ACID_BUCKET)
        .block(AddonBlocks.VOID_ACID)
        .tickRate(10)
        .slopeFindDistance(4)
        .levelDecreasePerBlock(1)
        .explosionResistance(100);

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
