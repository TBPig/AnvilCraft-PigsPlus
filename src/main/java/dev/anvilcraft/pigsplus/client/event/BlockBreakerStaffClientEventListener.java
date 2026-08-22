package dev.anvilcraft.pigsplus.client.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.item.BlockBreakerStaffItem;
import dev.anvilcraft.pigsplus.network.BlockBreakerStaffBreakPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID, value = Dist.CLIENT)
public class BlockBreakerStaffClientEventListener {
    private BlockBreakerStaffClientEventListener() {
    }

    @SubscribeEvent
    public static void breakBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getItemStack().getItem() instanceof BlockBreakerStaffItem)) return;

        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START
            || event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.CLIENT_HOLD) {
            PacketDistributor.sendToServer(new BlockBreakerStaffBreakPacket(
                event.getHand(),
                event.getPos()
            ));
        }
        event.setCanceled(true);
    }
}
