package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.inventory.ExperienceInterfaceMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record ExperienceInterfaceUpdatePacket(int value) implements IServerboundPacket {
    public static final Type<ExperienceInterfaceUpdatePacket> TYPE = IPacket.type(AnvilCraftPigsPlus.of("experience_interface_update"));
    public static final StreamCodec<ByteBuf, ExperienceInterfaceUpdatePacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        ExperienceInterfaceUpdatePacket::value,
        ExperienceInterfaceUpdatePacket::new
    );

    @Override
    public Type<ExperienceInterfaceUpdatePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(Player player) {
        if (!(player.containerMenu instanceof ExperienceInterfaceMenu menu)) return;
        menu.setXpTarget(this.value);
    }
}
