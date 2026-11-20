package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.PortableSilencerMenu;
import dev.anvilcraft.pigsplus.network.PortableSilencerSyncPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 便携消音器：屏蔽列表存放在 {@link MutedSounds} 组件上，携带在背包或饰品栏时为携带者屏蔽这些声音。
 */
public class PortableSilencerItem extends Item {
    public PortableSilencerItem(Properties properties) {
        super(properties);
    }

    /**
     * 解析玩家手上持有消音器的那只手，主手优先。
     */
    public static @Nullable InteractionHand findHand(Player player) {
        if (player.getMainHandItem().getItem() instanceof PortableSilencerItem) return InteractionHand.MAIN_HAND;
        if (player.getOffhandItem().getItem() instanceof PortableSilencerItem) return InteractionHand.OFF_HAND;
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        AddonMenuTypes.open(
            serverPlayer,
            new SimpleMenuProvider(
                (id, inventory, ignored) -> new PortableSilencerMenu(id, inventory),
                Component.translatable("screen.anvilcraft_pigsplus.portable_silencer.title")
            )
        );
        PacketDistributor.sendToPlayer(serverPlayer, new PortableSilencerSyncPacket(MutedSounds.get(stack)));
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        TooltipContext context,
        List<Component> tooltipComponents,
        TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.portable_silencer"
        ).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.portable_silencer.configure"
        ).withStyle(ChatFormatting.GRAY));
    }
}
