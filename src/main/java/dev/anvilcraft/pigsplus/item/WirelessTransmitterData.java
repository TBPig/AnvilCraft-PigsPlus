package dev.anvilcraft.pigsplus.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public record WirelessTransmitterData(BlockPos pos) {
    public static final Codec<WirelessTransmitterData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
        BlockPos.CODEC
            .fieldOf("pos")
            .forGetter(WirelessTransmitterData::pos)
    ).apply(ins, WirelessTransmitterData::new));

    public static final StreamCodec<ByteBuf, WirelessTransmitterData> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        WirelessTransmitterData::pos,
        WirelessTransmitterData::new
    );
}
