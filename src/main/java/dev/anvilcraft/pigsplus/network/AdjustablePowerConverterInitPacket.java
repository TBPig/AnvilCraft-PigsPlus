package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.gui.screen.AdjustablePowerConverterScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record AdjustablePowerConverterInitPacket(int value, int max) implements IClientboundPacket {
    public static final Type<AdjustablePowerConverterInitPacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("adjustable_power_converter_init"));
    public static final StreamCodec<ByteBuf, AdjustablePowerConverterInitPacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,
            AdjustablePowerConverterInitPacket::value,
            ByteBufCodecs.INT,
            AdjustablePowerConverterInitPacket::max,
            AdjustablePowerConverterInitPacket::new
        );

    @Override
    public Type<AdjustablePowerConverterInitPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof AdjustablePowerConverterScreen screen) {
            screen.initFromServer(this.value, this.max);
        }
    }
}
