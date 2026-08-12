package dev.anvilcraft.pigsplus.integration.curios;

import dev.anvilcraft.pigsplus.init.AddonItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

public final class CuriosCompat {
    private static final String CURIOS_MOD_ID = "curios";

    private CuriosCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(CURIOS_MOD_ID);
    }

    public static boolean hasPortableWirelessCharger(Player player) {
        if (!isLoaded()) {
            return false;
        }
        return CuriosHolder.hasPortableWirelessCharger(player);
    }

    private static final class CuriosHolder {
        private CuriosHolder() {
        }

        private static boolean hasPortableWirelessCharger(Player player) {
            return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.isEquipped(
                    stack -> ItemStack.isSameItemSameComponents(
                        AddonItems.PORTABLE_WIRELESS_CHARGER.asStack(), stack)))
                .orElse(false);
        }
    }
}
