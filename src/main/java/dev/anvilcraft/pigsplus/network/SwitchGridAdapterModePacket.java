package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.GridAdapterItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record SwitchGridAdapterModePacket(int mode) implements IServerboundPacket {
    public static final Type<SwitchGridAdapterModePacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("switch_grid_adapter_mode"));
    public static final StreamCodec<ByteBuf, SwitchGridAdapterModePacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SwitchGridAdapterModePacket::mode,
            SwitchGridAdapterModePacket::new
        );

    @Override
    public Type<SwitchGridAdapterModePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(Player player) {
        if (this.mode != GridAdapterItem.INPUT_MODE
            && this.mode != GridAdapterItem.OUTPUT_MODE) {
            return;
        }
        GridAdapterItem.setMode(player, this.mode);
    }
}
