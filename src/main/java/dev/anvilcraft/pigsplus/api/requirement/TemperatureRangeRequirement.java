package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.Temperature;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 温度区间需求，边界为可选的 {@link Temperature}。
 */
public class TemperatureRangeRequirement extends ReformerRequirement {
    public static final MapCodec<TemperatureRangeRequirement> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            temperatureCodec().optionalFieldOf("min").forGetter(req -> Optional.ofNullable(req.min)),
            temperatureCodec().optionalFieldOf("max").forGetter(req -> Optional.ofNullable(req.max))
        ).apply(instance, (min, max) -> new TemperatureRangeRequirement(min.orElse(null), max.orElse(null))));

    private final @Nullable Temperature min;
    private final @Nullable Temperature max;

    public TemperatureRangeRequirement() {
        this(null, null);
    }

    public TemperatureRangeRequirement(@Nullable Temperature min, @Nullable Temperature max) {
        this.min = min;
        this.max = max;
    }

    private static Codec<Temperature> temperatureCodec() {
        return Codec.STRING.xmap(Temperature::fromName, Temperature::getSerializedName);
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return CODEC;
    }

    @Override
    public Component getDescription() {
        if (this.min != null && this.max != null) {
            return this.text(
                "requirement.anvilcraft_pigsplus.temperature_between",
                this.min.getSerializedName(),
                this.max.getSerializedName()
            );
        }
        if (this.min != null) {
            return this.text("requirement.anvilcraft_pigsplus.temperature_at_least", this.min.getSerializedName());
        }
        if (this.max != null) {
            return this.text("requirement.anvilcraft_pigsplus.temperature_at_most", this.max.getSerializedName());
        }
        return this.text("requirement.anvilcraft_pigsplus.unknown");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("temperature");
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        Temperature temperature;
        if (body instanceof RockyPlanetData rp) {
            temperature = rp.temperature();
        } else if (body instanceof SpecialCelestialBodyData sp) {
            temperature = sp.temperature();
        } else {
            return false;
        }
        int value = temperature.ordinal();
        int min = this.min == null ? 0 : this.min.ordinal();
        int max = this.max == null ? Temperature.values().length - 1 : this.max.ordinal();
        return value >= min && value <= max;
    }
}
