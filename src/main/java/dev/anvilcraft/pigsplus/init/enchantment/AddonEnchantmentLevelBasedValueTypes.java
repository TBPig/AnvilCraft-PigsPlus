package dev.anvilcraft.pigsplus.init.enchantment;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.enchantment.SqrtIncreaseValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonEnchantmentLevelBasedValueTypes {
    private static final DeferredRegister<MapCodec<? extends LevelBasedValue>> REGISTER =
        DeferredRegister.create(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, AnvilCraftPigsPlus.MOD_ID);

    static {
        REGISTER.register("sqrt_increase_value", () -> SqrtIncreaseValue.CODEC);
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
