package dev.anvilcraft.pigsplus.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.integration.curios.CuriosItemTags;
import net.minecraft.world.item.Item;

public class ItemTagLoader {
    public static void init(RegistrumTagsProvider<Item> provider) {
        provider.addTag(CuriosItemTags.CHARM)
            .replace(false)
            .add(AddonItems.PORTABLE_WIRELESS_CHARGER.getKey());
    }
}
