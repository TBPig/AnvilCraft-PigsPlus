package dev.anvilcraft.pigsplus.network;

import dev.anvilcraft.lib.v2.codec.StreamCodecUtil;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import dev.anvilcraft.lib.v2.network.packet.IServerboundPacket;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.BlockBreakerStaffItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record BlockBreakerStaffBreakPacket(
    InteractionHand hand,
    BlockPos pos
) implements IServerboundPacket {
    private static final int COOLDOWN_TICKS = 8;

    public static final Type<BlockBreakerStaffBreakPacket> TYPE =
        IPacket.type(AnvilCraftPigsPlus.of("block_breaker_staff_break"));
    public static final StreamCodec<ByteBuf, BlockBreakerStaffBreakPacket> STREAM_CODEC =
        StreamCodec.composite(
            StreamCodecUtil.enumStreamCodec(InteractionHand.class),
            BlockBreakerStaffBreakPacket::hand,
            BlockPos.STREAM_CODEC,
            BlockBreakerStaffBreakPacket::pos,
            BlockBreakerStaffBreakPacket::new
        );

    @Override
    public Type<BlockBreakerStaffBreakPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        ServerLevel level = serverPlayer.serverLevel();
        if (!level.isLoaded(this.pos)) return;

        ItemStack stack = player.getItemInHand(this.hand);
        if (!(stack.getItem() instanceof BlockBreakerStaffItem)) return;
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return;

        int durabilityCost = BlockBreakerStaffItem.tryBreakBlock(level, player, stack, this.pos);
        if (durabilityCost > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);
            stack.hurtAndBreak(durabilityCost, player, LivingEntity.getSlotForHand(this.hand));
        }
    }
}
