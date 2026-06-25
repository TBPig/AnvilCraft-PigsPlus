package dev.anvilcraft.pigsplus.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.MOD_ID;

public class AddonParticleTypes {
    public static final DeferredRegister<ParticleType<?>> REGISTER =
        DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);

    public static final Supplier<SimpleParticleType> EXP =
        REGISTER.register("exp", () -> new SimpleParticleType(false));

    public static void register(IEventBus modBus) {
        REGISTER.register(modBus);
    }
}
