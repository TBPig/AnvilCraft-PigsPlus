package dev.anvilcraft.pigsplus.integration.curios;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CuriosItemTags {
    public static final TagKey<Item> BRACELET = create("bracelet");
    public static final TagKey<Item> HANDS = create("hands");
    public static final TagKey<Item> RING = create("ring");
    public static final TagKey<Item> CHARM = create("charm");

    private CuriosItemTags() {
    }

    private static TagKey<Item> create(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", path));
    }
}
