package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.gui.screen.PortableWirelessChargerScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record PortableWirelessChargerInitPacket(int value, int max) implements IClientboundPacket {
    public static final Type<PortableWirelessChargerInitPacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("portable_wireless_charger_init"));
    public static final StreamCodec<ByteBuf, PortableWirelessChargerInitPacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,
            PortableWirelessChargerInitPacket::value,
            ByteBufCodecs.INT,
            PortableWirelessChargerInitPacket::max,
            PortableWirelessChargerInitPacket::new
        );

    @Override
    public Type<PortableWirelessChargerInitPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof PortableWirelessChargerScreen screen) {
            screen.initFromServer(this.value, this.max);
        }
    }
}
