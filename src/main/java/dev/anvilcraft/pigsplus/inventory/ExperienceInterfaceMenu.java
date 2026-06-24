package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.dubhe.anvilcraft.util.Callback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ExperienceInterfaceMenu extends AbstractContainerMenu {
    private @Nullable final Callback<Integer> callback;

    public ExperienceInterfaceMenu(int containerId, Callback<Integer> callback) {
        super(AddonMenuTypes.EXPERIENCE_INTERFACE.get(), containerId);
        this.callback = callback;
    }

    public ExperienceInterfaceMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
        this.callback = null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void setXpTarget(int value) {
        if (this.callback != null) {
            this.callback.onValueChange(value);
        }
    }
}
