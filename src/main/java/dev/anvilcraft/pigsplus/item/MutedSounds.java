package dev.anvilcraft.pigsplus.item;

import com.mojang.serialization.Codec;
import dev.anvilcraft.pigsplus.init.AddonDataComponents;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 消音器的屏蔽列表组件：数据自身保证去重、不可变与容量上限，任意物品挂上该组件即可参与静音。
 */
public record MutedSounds(List<ResourceLocation> sounds) {
    public static final int MAX_SIZE = 256;
    public static final MutedSounds EMPTY = new MutedSounds(List.of());

    public static final Codec<MutedSounds> CODEC = ResourceLocation.CODEC
        .sizeLimitedListOf(MAX_SIZE)
        .xmap(MutedSounds::of, MutedSounds::sounds);

    public static final StreamCodec<ByteBuf, MutedSounds> STREAM_CODEC = ResourceLocation.STREAM_CODEC
        .apply(ByteBufCodecs.list(MAX_SIZE))
        .map(MutedSounds::of, MutedSounds::sounds);

    public MutedSounds {
        sounds = List.copyOf(sounds);
    }

    /**
     * 读取存档或网络数据时统一去重并截断
     */
    public static MutedSounds of(Collection<ResourceLocation> sounds) {
        if (sounds.isEmpty()) return EMPTY;
        List<ResourceLocation> unique = List.copyOf(new LinkedHashSet<>(sounds));
        if (unique.size() <= MAX_SIZE) return new MutedSounds(unique);
        return new MutedSounds(List.copyOf(unique.subList(0, MAX_SIZE)));
    }

    public static MutedSounds get(ItemStack stack) {
        return stack.getOrDefault(AddonDataComponents.MUTED_SOUNDS, EMPTY);
    }

    public static void set(ItemStack stack, MutedSounds sounds) {
        stack.set(AddonDataComponents.MUTED_SOUNDS, sounds);
    }

    public int size() {
        return this.sounds.size();
    }

    public boolean isEmpty() {
        return this.sounds.isEmpty();
    }

    public boolean contains(ResourceLocation sound) {
        return this.sounds.contains(sound);
    }

    /**
     * 已存在或已达上限时返回自身，调用方可用相等判断是否发生变化。
     */
    public MutedSounds withSound(ResourceLocation sound) {
        if (this.contains(sound) || this.size() >= MAX_SIZE) return this;
        LinkedHashSet<ResourceLocation> updated = new LinkedHashSet<>(this.sounds);
        updated.add(sound);
        return new MutedSounds(List.copyOf(updated));
    }

    public MutedSounds withoutSound(ResourceLocation sound) {
        if (!this.contains(sound)) return this;
        LinkedHashSet<ResourceLocation> updated = new LinkedHashSet<>(this.sounds);
        updated.remove(sound);
        return of(updated);
    }
}
