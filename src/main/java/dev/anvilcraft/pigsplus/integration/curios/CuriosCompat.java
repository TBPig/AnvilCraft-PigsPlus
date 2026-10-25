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
        List<ItemStack> chargers = new ArrayList<>();
        for (ItemStack stack : getEquippedItems(player)) {
            if (stack.is(AddonItems.PORTABLE_WIRELESS_CHARGER.get())) {
                chargers.add(stack);
            }
        }
        return chargers;
    }

    public static List<ItemStack> getEquippedItems(Player player) {
        if (!isLoaded()) {
            return List.of();
        }
        return CuriosHolder.getEquippedItems(player);
    }

    private static final class CuriosHolder {
        private CuriosHolder() {
        }

        private static List<ItemStack> getEquippedItems(Player player) {
            return CuriosApi.getCuriosInventory(player)
                .map(handler -> {
                    var equippedCurios = handler.getEquippedCurios();
                    List<ItemStack> items = new ArrayList<>(equippedCurios.getSlots());
                    for (int i = 0; i < equippedCurios.getSlots(); i++) {
                        items.add(equippedCurios.getStackInSlot(i));
                    }
                    return items;
                })
                .orElse(List.of());
        }
    }
}
