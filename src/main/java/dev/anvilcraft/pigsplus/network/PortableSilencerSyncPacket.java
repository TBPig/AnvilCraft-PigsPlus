package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.gui.screen.PortableSilencerScreen;
import dev.anvilcraft.pigsplus.item.MutedSounds;
import dev.anvilcraft.pigsplus.item.PortableSilencerItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record PortableSilencerSyncPacket(MutedSounds sounds) implements IClientboundPacket {
    public static final Type<PortableSilencerSyncPacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("portable_silencer_sync"));
    public static final StreamCodec<ByteBuf, PortableSilencerSyncPacket> STREAM_CODEC =
        MutedSounds.STREAM_CODEC.map(PortableSilencerSyncPacket::new, PortableSilencerSyncPacket::sounds);

    @Override
    public Type<PortableSilencerSyncPacket> type() {
        return TYPE;
    }

    /**
     * 服务端只在改动后回发本包，因此这里同时刷新界面并镜像到手持物品，使改动立即生效。
     */
    @Override
    public void handleOnClient(Player player) {
        @Nullable InteractionHand hand = PortableSilencerItem.findHand(player);
        if (hand != null) {
            MutedSounds.set(player.getItemInHand(hand), this.sounds);
        }
        if (Minecraft.getInstance().screen instanceof PortableSilencerScreen screen) {
            screen.handleSync(this.sounds);
        }
    }
}
