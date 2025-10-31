package dev.anvilcraft.pigsplus.init;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

public class AddonBlocks {
    static {
        REGISTRATE.defaultCreativeTab(AddonItemGroups.ADDON_ITEMS.getKey());
    }

    public static final BlockEntry<Block> EXAMPLE_BLOCK = REGISTRATE
        .block("example_block", Block::new)
        .simpleItem()
        .register();

    public static void register() {
    }
}
