package dev.anvilcraft.pigsplus.integration.ae2;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class Ae2BlockTags {
    public static final TagKey<Block> GROWTH_ACCELERATABLE = TagKey.create(
        Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("ae2", "growth_acceleratable"));

    private Ae2BlockTags() {
    }
}
