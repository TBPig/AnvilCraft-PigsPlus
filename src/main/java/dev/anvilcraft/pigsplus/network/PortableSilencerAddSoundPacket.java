package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.inventory.PortableSilencerMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record PortableSilencerAddSoundPacket(ResourceLocation soundId) implements IServerboundPacket {
    public static final Type<PortableSilencerAddSoundPacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("portable_silencer_add_sound"));
    public static final StreamCodec<ByteBuf, PortableSilencerAddSoundPacket> STREAM_CODEC =
        StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            PortableSilencerAddSoundPacket::soundId,
            PortableSilencerAddSoundPacket::new
        );

    @Override
    public Type<PortableSilencerAddSoundPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(Player player) {
        if (player.containerMenu instanceof PortableSilencerMenu menu) {
            menu.addSound(this.soundId);
        }
    }
}
