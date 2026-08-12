package dev.anvilcraft.pigsplus.integration.curios;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CuriosItemTags {
    public static final TagKey<Item> CHARM = TagKey.create(
        Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "charm"));

    private CuriosItemTags() {
    }
}
