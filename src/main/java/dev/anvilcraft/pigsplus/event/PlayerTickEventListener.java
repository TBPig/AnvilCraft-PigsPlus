package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.item.PortableWirelessChargerItem;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class PlayerTickEventListener {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PortableWirelessChargerItem.playerTick(serverPlayer);
        }
    }
}
