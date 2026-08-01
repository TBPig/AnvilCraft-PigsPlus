package dev.anvilcraft.pigsplus.api.requirement;

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

import java.util.function.Function;

/**
 * 配方中的一条需求：由需求 id 和反序列化出的需求实例组成。
 *
 * <p>data.json 中既支持纯字符串 id（无参数需求），也支持对象形式
 * {@code {"requirement": "id", ...参数}}；参数由对应需求的 {@link ReformerRequirement#codec()} 解码。</p>
 */
public record RequirementEntry(ResourceLocation id, ReformerRequirement requirement) {
    private static final Codec<RequirementEntry> STRING_CODEC = ResourceLocation.CODEC.flatXmap(
        RequirementEntry::fromId,
        entry -> DataResult.success(entry.id())
    );

    private static final Codec<RequirementEntry> OBJECT_CODEC = Codec.STRING.dispatch(
        "requirement",
        (Function<RequirementEntry, String>) entry -> entry.id().toString(),
        (Function<String, MapCodec<RequirementEntry>>) id -> RequirementEntry.codecFor(id)
    );

    public static final Codec<RequirementEntry> CODEC = Codec.either(STRING_CODEC, OBJECT_CODEC)
        .xmap(either -> either.map(Function.identity(), Function.identity()), RequirementEntry::toEither)
        .validate(RequirementEntry::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, RequirementEntry> STREAM_CODEC =
        StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            RequirementEntry::id,
            ByteBufCodecs.COMPOUND_TAG,
            RequirementEntry::writeTag,
            RequirementEntry::readTag
        );

    private static DataResult<RequirementEntry> fromId(ResourceLocation id) {
        ReformerRequirement prototype = CelestialReformerRequirements.REGISTRY.get(id);
        if (prototype == null) {
            return DataResult.error(() -> "Unknown reformer requirement: " + id);
        }
        return DataResult.success(new RequirementEntry(id, prototype));
    }

    @SuppressWarnings("unchecked")
    private static MapCodec<RequirementEntry> codecFor(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        ReformerRequirement prototype = location == null
                                        ? null
                                        : CelestialReformerRequirements.REGISTRY.get(location);
        if (prototype == null) {
            return MapCodec.unit(() -> new RequirementEntry(location, null));
        }
        MapCodec<ReformerRequirement> dataCodec = (MapCodec<ReformerRequirement>) prototype.codec();
        return dataCodec.xmap(
            requirement -> new RequirementEntry(location, requirement),
            RequirementEntry::requirement
        );
    }

    private static Either<RequirementEntry, RequirementEntry> toEither(RequirementEntry entry) {
        return Either.right(entry);
    }

    private static DataResult<RequirementEntry> validate(RequirementEntry entry) {
        return entry.requirement() == null
               ? DataResult.error(() -> "Unknown reformer requirement: " + entry.id())
               : DataResult.success(entry);
    }

    private static CompoundTag writeTag(RequirementEntry entry) {
        return CODEC.encodeStart(NbtOps.INSTANCE, entry).result()
            .map(tag -> tag instanceof CompoundTag compound ? compound : new CompoundTag())
            .orElseGet(CompoundTag::new);
    }

    private static RequirementEntry readTag(ResourceLocation id, CompoundTag tag) {
        RequirementEntry entry = CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
        return entry == null ? new RequirementEntry(id, null) : entry;
    }
}
