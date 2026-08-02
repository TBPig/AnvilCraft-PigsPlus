package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 转速等级需求，支持可选的上下限；未填写的边界表示不限制。
 */
public class RotationSpeedRequirement extends ReformerRequirement {
    public static final MapCodec<RotationSpeedRequirement> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("min").forGetter(requirement -> Optional.ofNullable(requirement.min)),
            Codec.INT.optionalFieldOf("max").forGetter(requirement -> Optional.ofNullable(requirement.max))
        ).apply(instance, (min, max) -> new RotationSpeedRequirement(min.orElse(null), max.orElse(null))));

    private final @Nullable Integer min;
    private final @Nullable Integer max;

    public RotationSpeedRequirement() {
        this(null, null);
    }

    public RotationSpeedRequirement(@Nullable Integer min, @Nullable Integer max) {
        this.min = min;
        this.max = max;
    }

    public @Nullable Integer min() {
        return this.min;
    }

    public @Nullable Integer max() {
        return this.max;
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return CODEC;
    }

    @Override
    public Component getDescription() {
        if (this.min != null && this.max != null) {
            return this.text("requirement.anvilcraft_pigsplus.rotation_speed_between", this.min, this.max);
        }
        if (this.min != null) {
            return this.text("requirement.anvilcraft_pigsplus.rotation_speed_at_least", this.min);
        }
        if (this.max != null) {
            return this.text("requirement.anvilcraft_pigsplus.rotation_speed_at_most", this.max);
        }
        return this.text("requirement.anvilcraft_pigsplus.unknown");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("rotation_speed");
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        if (be.getCelestialBodyData() == null) return false;
        int value = be.getCelestialBodyData().rotationSpeed();
        return (this.min == null || value >= this.min) && (this.max == null || value <= this.max);
    }
}
