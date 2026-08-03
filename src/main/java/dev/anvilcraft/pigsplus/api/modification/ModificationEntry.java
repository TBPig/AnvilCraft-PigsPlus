package dev.anvilcraft.pigsplus.api.modification;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * 配方中的一条改造：由改造 id 和反序列化出的改造实例组成。
 *
 * <p>data.json 中既支持纯字符串 id（无参数改造），也支持对象形式
 * {@code {"modification": "id", ...参数}}；参数由对应改造的 {@link ReformerModification#codec()} 解码。</p>
 */
public record ModificationEntry(ResourceLocation id, ReformerModification modification) {
    private static final Codec<ModificationEntry> STRING_CODEC = ResourceLocation.CODEC.flatXmap(
        ModificationEntry::fromId,
        entry -> DataResult.success(entry.id())
    );

    private static final Codec<ModificationEntry> OBJECT_CODEC = Codec.STRING.dispatch(
        "modification",
        (Function<ModificationEntry, String>) entry -> entry.id().toString(),
        (Function<String, MapCodec<ModificationEntry>>) ModificationEntry::codecFor
    );

    public static final Codec<ModificationEntry> CODEC = Codec.either(STRING_CODEC, OBJECT_CODEC)
        .xmap(either -> either.map(Function.identity(), Function.identity()), ModificationEntry::toEither)
        .validate(ModificationEntry::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModificationEntry> STREAM_CODEC =
        StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ModificationEntry::id,
            ByteBufCodecs.COMPOUND_TAG,
            ModificationEntry::writeTag,
            ModificationEntry::readTag
        );

    private static DataResult<ModificationEntry> fromId(ResourceLocation id) {
        ReformerModification prototype = ReformerModifications.REGISTRY.get(id);
        if (prototype == null) {
            return DataResult.error(() -> "Unknown reformer modification: " + id);
        }
        return DataResult.success(new ModificationEntry(id, prototype));
    }

    @SuppressWarnings("unchecked")
    private static MapCodec<ModificationEntry> codecFor(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        ReformerModification prototype = location == null
                                         ? null
                                         : ReformerModifications.REGISTRY.get(location);
        if (prototype == null) {
            return MapCodec.unit(() -> new ModificationEntry(location, null));
        }
        MapCodec<ReformerModification> dataCodec = (MapCodec<ReformerModification>) prototype.codec();
        return dataCodec.xmap(
            modification -> new ModificationEntry(location, modification),
            ModificationEntry::modification
        );
    }

    private static Either<ModificationEntry, ModificationEntry> toEither(ModificationEntry entry) {
        return Either.right(entry);
    }

    private static DataResult<ModificationEntry> validate(ModificationEntry entry) {
        return entry.modification() == null
               ? DataResult.error(() -> "Unknown reformer modification: " + entry.id())
               : DataResult.success(entry);
    }

    private static CompoundTag writeTag(ModificationEntry entry) {
        return CODEC.encodeStart(NbtOps.INSTANCE, entry).result()
            .map(tag -> tag instanceof CompoundTag compound ? compound : new CompoundTag())
            .orElseGet(CompoundTag::new);
    }

    private static ModificationEntry readTag(ResourceLocation id, CompoundTag tag) {
        ModificationEntry entry = CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
        return entry == null ? new ModificationEntry(id, null) : entry;
    }

    public @Nullable ReformerModification resolved() {
        return this.modification != null
               ? this.modification
               : ReformerModifications.REGISTRY.get(this.id);
    }
}
