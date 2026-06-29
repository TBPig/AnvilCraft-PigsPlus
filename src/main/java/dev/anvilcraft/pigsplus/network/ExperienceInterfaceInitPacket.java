package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.gui.screen.ExperienceInterfaceScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record ExperienceInterfaceInitPacket(int value) implements IClientboundPacket {
    public static final Type<ExperienceInterfaceInitPacket> TYPE = IPacket.type(AnvilCraftPigsPlus.of("experience_interface_init"));
    public static final StreamCodec<ByteBuf, ExperienceInterfaceInitPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        ExperienceInterfaceInitPacket::value,
        ExperienceInterfaceInitPacket::new
    );

    @Override
    public Type<ExperienceInterfaceInitPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (!(Minecraft.getInstance().screen instanceof ExperienceInterfaceScreen screen)) return;
        screen.setValue(this.value);
    }
}
