package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PortableWirelessChargerMenu extends AbstractContainerMenu {
    public PortableWirelessChargerMenu(int containerId) {
        super(AddonMenuTypes.PORTABLE_WIRELESS_CHARGER.get(), containerId);
    }

    public PortableWirelessChargerMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
