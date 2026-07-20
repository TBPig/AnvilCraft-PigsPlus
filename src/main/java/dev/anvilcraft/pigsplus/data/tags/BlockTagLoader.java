package dev.anvilcraft.pigsplus.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.pigsplus.init.AddonBlockTags;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockTagLoader {
    private static ResourceKey<Block> findResourceKey(Block block) {
        return ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(block));
    }

    public static void init(RegistrumTagsProvider<Block> provider) {
        provider.addTag(AddonBlockTags.VOID_ACID_IMMUNE)
            .add(findResourceKey(Blocks.AIR))
            .add(findResourceKey(Blocks.CAVE_AIR))
            .add(findResourceKey(Blocks.VOID_AIR))
            .add(findResourceKey(Blocks.BEDROCK))
            .add(AddonBlocks.VOID_ACID.getKey())
            .add(ModBlocks.NEGATIVE_MATTER_BLOCK.getKey())
            .add(ModBlocks.VOID_ENERGY_COLLECTOR.getKey())
            .add(ModBlocks.VOID_STONE.getKey())
            .add(ModBlocks.VOID_MATTER_BLOCK.getKey())
            .add(ModBlocks.TRANSCENDIUM_BLOCK.getKey());
    }
}
