package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.megastructure.ReformerHandler;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.megastructure.IMegastructureHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public record CelestialReformerSyncPacket(BlockPos pos, CompoundTag tag) implements IClientboundPacket {
    public static final Type<CelestialReformerSyncPacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("celestial_reformer_sync"));
    public static final StreamCodec<ByteBuf, CelestialReformerSyncPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        CelestialReformerSyncPacket::pos,
        ByteBufCodecs.COMPOUND_TAG,
        CelestialReformerSyncPacket::tag,
        CelestialReformerSyncPacket::new
    );

    @Override
    public Type<CelestialReformerSyncPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        BlockEntity be = player.level().getBlockEntity(this.pos);
        if (be instanceof CelestialForgingAnvilBlockEntity anvil) {
            IMegastructureHandler active = anvil.getMegastructureManager().getActiveHandler(anvil);
            if (active instanceof ReformerHandler handler) {
                handler.readUpdateTag(this.tag, player.level().registryAccess());
            }
        }
    }
}
