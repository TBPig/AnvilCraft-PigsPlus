package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.PortableWirelessChargerItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record PortableWirelessChargerUpdatePacket(int value) implements IServerboundPacket {
    public static final Type<PortableWirelessChargerUpdatePacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("portable_wireless_charger_update"));
    public static final StreamCodec<ByteBuf, PortableWirelessChargerUpdatePacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.INT,
            PortableWirelessChargerUpdatePacket::value,
            PortableWirelessChargerUpdatePacket::new
        );

    @Override
    public Type<PortableWirelessChargerUpdatePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(Player player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof PortableWirelessChargerItem) {
            PortableWirelessChargerItem.setPower(stack, this.value);
            if (player instanceof ServerPlayer serverPlayer) {
                PortableWirelessChargerItem.refreshPower(serverPlayer);
            }
        }
    }
}
