package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AddonBlockTags {
    public static final TagKey<Block> VOID_ACID_IMMUNE = bind("void_acid_immune");

    private static TagKey<Block> bind(String id) {
        return TagKey.create(Registries.BLOCK, AnvilCraftPigsPlus.of(id));
    }
}
