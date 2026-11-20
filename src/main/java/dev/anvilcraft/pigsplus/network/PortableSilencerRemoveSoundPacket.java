package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.inventory.PortableSilencerMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record PortableSilencerRemoveSoundPacket(ResourceLocation soundId) implements IServerboundPacket {
    public static final Type<PortableSilencerRemoveSoundPacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("portable_silencer_remove_sound"));
    public static final StreamCodec<ByteBuf, PortableSilencerRemoveSoundPacket> STREAM_CODEC =
        StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            PortableSilencerRemoveSoundPacket::soundId,
            PortableSilencerRemoveSoundPacket::new
        );

    @Override
    public Type<PortableSilencerRemoveSoundPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(Player player) {
        if (player.containerMenu instanceof PortableSilencerMenu menu) {
            menu.removeSound(this.soundId);
        }
    }
}
