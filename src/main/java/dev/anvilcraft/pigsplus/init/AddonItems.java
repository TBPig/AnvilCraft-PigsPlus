package dev.anvilcraft.pigsplus.init;

import dev.dubhe.anvilcraft.AnvilCraft;

public class AddonItems {
    static {
        AnvilCraft.REGISTRATE.defaultCreativeTab(AddonItemGroups.ADDON_ITEMS.getKey());
    }

    public static void register() {
    }
}
