package dev.anvilcraft.pigsplus.integration.curios;

import dev.anvilcraft.pigsplus.init.AddonItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public final class CuriosCompat {
    private static final String CURIOS_MOD_ID = "curios";

    private CuriosCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(CURIOS_MOD_ID);
    }

    public static boolean hasPortableWirelessCharger(Player player) {
        return !getPortableWirelessChargers(player).isEmpty();
    }

    public static List<ItemStack> getPortableWirelessChargers(Player player) {
        if (!isLoaded()) {
            return List.of();
        }
        return CuriosHolder.getPortableWirelessChargers(player);
    }

    private static final class CuriosHolder {
        private CuriosHolder() {
        }

        private static List<ItemStack> getPortableWirelessChargers(Player player) {
            return CuriosApi.getCuriosInventory(player)
                .map(handler -> {
                    List<ItemStack> chargers = new ArrayList<>();
                    var equippedCurios = handler.getEquippedCurios();
                    for (int i = 0; i < equippedCurios.getSlots(); i++) {
                        ItemStack stack = equippedCurios.getStackInSlot(i);
                        if (stack.is(AddonItems.PORTABLE_WIRELESS_CHARGER.get())) {
                            chargers.add(stack);
                        }
                    }
                    return chargers;
                })
                .orElse(List.of());
        }
    }
}
