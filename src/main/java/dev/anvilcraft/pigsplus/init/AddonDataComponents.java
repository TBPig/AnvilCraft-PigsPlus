package dev.anvilcraft.pigsplus.init;

import com.mojang.serialization.Codec;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.WirelessTransmitterData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class AddonDataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AnvilCraftPigsPlus.MOD_ID);

    public static final DataComponentType<Integer> GRID_ADAPTER_POWER = register(
        "grid_adapter_power",
        builder -> builder
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<WirelessTransmitterData> WIRELESS_TRANSMITTER_TARGET = register(
        "wireless_transmitter_target",
        builder -> builder
            .persistent(WirelessTransmitterData.CODEC)
            .networkSynchronized(WirelessTransmitterData.STREAM_CODEC)
    );

    private AddonDataComponents() {
    }

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        DataComponentType.Builder<T> builder = DataComponentType.builder();
        customizer.accept(builder);
        DataComponentType<T> type = builder.build();
        DATA_COMPONENT_TYPES.register(name, () -> type);
        return type;
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}
