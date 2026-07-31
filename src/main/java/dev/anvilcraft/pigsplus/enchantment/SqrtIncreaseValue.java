package dev.anvilcraft.pigsplus.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record SqrtIncreaseValue(float scale) implements LevelBasedValue {
    public static final MapCodec<SqrtIncreaseValue> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("scale", 1.0f).forGetter(SqrtIncreaseValue::scale)
        ).apply(instance, SqrtIncreaseValue::new)
    );

    @Override
    public float calculate(int level) {
        if (level <= 0) return 0.0f;
        return (float) (Math.sqrt(level) * this.scale);
    }

    @Override
    public MapCodec<SqrtIncreaseValue> codec() {
        return CODEC;
    }
}
