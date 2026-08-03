package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.gui.screen.GridAdapterScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record GridAdapterInitPacket(int value, int max) implements IClientboundPacket {
    public static final Type<GridAdapterInitPacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("grid_adapter_init"));
    public static final StreamCodec<ByteBuf, GridAdapterInitPacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,
            GridAdapterInitPacket::value,
            ByteBufCodecs.INT,
            GridAdapterInitPacket::max,
            GridAdapterInitPacket::new
        );

    @Override
    public Type<GridAdapterInitPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof GridAdapterScreen screen) {
            screen.initFromServer(this.value, this.max);
        }
    }
}
