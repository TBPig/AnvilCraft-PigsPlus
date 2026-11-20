package dev.anvilcraft.pigsplus.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.integration.curios.CuriosItemTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ItemTagLoader {
    public static void init(RegistrumTagsProvider<Item> provider) {
        ResourceKey<Item> silencer = AddonItems.PORTABLE_SILENCER.getKey();

        provider.addTag(CuriosItemTags.CHARM)
            .replace(false)
            .add(AddonItems.PORTABLE_WIRELESS_CHARGER.getKey(), silencer);
        provider.addTag(CuriosItemTags.BRACELET).replace(false).add(silencer);
        provider.addTag(CuriosItemTags.HANDS).replace(false).add(silencer);
        provider.addTag(CuriosItemTags.RING).replace(false).add(silencer);
    }
}
