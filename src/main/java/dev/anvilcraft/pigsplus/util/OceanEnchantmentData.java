package dev.anvilcraft.pigsplus.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 液态魔咒海洋携带的附魔数据。
 */
public record OceanEnchantmentData(@Nullable ResourceKey<Enchantment> enchantment) {
    public static final OceanEnchantmentData EMPTY = new OceanEnchantmentData(null);

    public static final Codec<OceanEnchantmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.ENCHANTMENT)
            .optionalFieldOf("enchantment")
            .forGetter(data -> Optional.ofNullable(data.enchantment))
    ).apply(instance, optional -> new OceanEnchantmentData(optional.orElse(null))));

    public static OceanEnchantmentData empty() {
        return EMPTY;
    }
}
