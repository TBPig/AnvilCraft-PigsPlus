package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.BlockBreakerStaffItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record SwitchBlockBreakerStaffModePacket(boolean protectContainers) implements IServerboundPacket {
    public static final Type<SwitchBlockBreakerStaffModePacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("switch_block_breaker_staff_mode"));
    public static final StreamCodec<ByteBuf, SwitchBlockBreakerStaffModePacket> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SwitchBlockBreakerStaffModePacket::protectContainers,
            SwitchBlockBreakerStaffModePacket::new
        );

    @Override
    public Type<SwitchBlockBreakerStaffModePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(Player player) {
        BlockBreakerStaffItem.setProtectsContainers(player, this.protectContainers);
    }
}
