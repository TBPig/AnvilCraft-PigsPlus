package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntry;
import dev.anvilcraft.pigsplus.block.AdjustablePowerConverterBlock;
import dev.anvilcraft.pigsplus.block.AutoChickenBlock;
import dev.anvilcraft.pigsplus.block.AutoJewelCraftingTableBlock;
import dev.anvilcraft.pigsplus.block.AutoRoyalGrindstoneBlock;
import dev.anvilcraft.pigsplus.block.AutoRoyalSmithingTableBlock;
import dev.anvilcraft.pigsplus.block.BlockBreakerBlock;
import dev.anvilcraft.pigsplus.block.BuddingEchoShardBlock;
import dev.anvilcraft.pigsplus.block.ChainSmithingTableBlock;
import dev.anvilcraft.pigsplus.block.EchoClusterBlock;
import dev.anvilcraft.pigsplus.block.ElectricEnchantingTableBlock;
import dev.anvilcraft.pigsplus.block.EnchantedGeneratorBlock;
import dev.anvilcraft.pigsplus.block.ExperienceInterfaceBlock;
import dev.anvilcraft.pigsplus.block.PigAnvilBlock;
import dev.anvilcraft.pigsplus.block.RedstoneConduitBlock;
import dev.anvilcraft.pigsplus.block.SculkExtractorBlock;
import dev.anvilcraft.pigsplus.block.VoidCatalystBlock;
import dev.anvilcraft.pigsplus.block.WeakResinBlock;
import dev.anvilcraft.pigsplus.block.item.WeakResinBlockItem;
import dev.anvilcraft.pigsplus.data.recipe.RegistrumBlockRecipeLoader;
import dev.dubhe.anvilcraft.init.block.ModBlockTags;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.util.registrater.DataGenUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.neoforged.neoforge.common.Tags;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

@SuppressWarnings("unused")
public class AddonBlocks {
    static {
        REGISTRATE.defaultCreativeTab(AddonItemGroups.ADDON_ITEMS.getKey());
    }

    public static final BlockEntry<PigAnvilBlock> PIG_ANVIL = REGISTRATE
        .block("pig_anvil", PigAnvilBlock::new)
        .initialProperties(() -> Blocks.CHERRY_PLANKS)
        .properties(p -> p.noOcclusion().isValidSpawn(Blocks::never))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .item()
        .tag(ItemTags.ANVIL)
        .build()
        .tag(BlockTags.ANVIL, BlockTags.MINEABLE_WITH_AXE, ModBlockTags.NON_MAGNETIC, ModBlockTags.CANT_BROKEN_ANVIL)
        .recipe(RegistrumBlockRecipeLoader::pigAnvil)
        .register();

    public static final BlockEntry<? extends Block> WEAK_RESIN_BLOCK = REGISTRATE
        .block("weak_resin_block", WeakResinBlock::new)
        .lang("Block of Weak Resin")
        .initialProperties(() -> Blocks.SLIME_BLOCK)
        .blockstate(DataGenUtil::noExtraModelOrState)
        .properties(properties -> properties.sound(SoundType.HONEY_BLOCK))
        .item(WeakResinBlockItem::new)
        .build()
        .recipe(RegistrumBlockRecipeLoader::weakResinBlock)
        .register();

    public static final BlockEntry<AutoChickenBlock> AUTO_CHICKEN = REGISTRATE
        .block("auto_chicken", AutoChickenBlock::new)
        .lang("Auto Chicken")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .register();

    public static final BlockEntry<RedstoneConduitBlock> REDSTONE_CONDUIT_BLOCK = REGISTRATE
        .block("redstone_conduit_block", RedstoneConduitBlock::new)
        .lang("Redstone Conduit Block")
        .initialProperties(() -> Blocks.STONE)
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .recipe(RegistrumBlockRecipeLoader::redstoneConduitBlock)
        .register();

    public static final BlockEntry<BlockBreakerBlock> BLOCK_BREAKER = REGISTRATE
        .block("block_breaker", BlockBreakerBlock::new)
        .lang("Block Breaker")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.noOcclusion().isValidSpawn(Blocks::never))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .recipe(RegistrumBlockRecipeLoader::blockBreaker)
        .register();

    public static final BlockEntry<ChainSmithingTableBlock> CHAIN_SMITHING_TABLE_BLOCK = REGISTRATE
        .block("chain_smithing_table", ChainSmithingTableBlock::new)
        .lang("Chain Smithing Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.strength(5.0f, 1200f))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
        .recipe(RegistrumBlockRecipeLoader::chainSmithingTableBlock)
        .register();

    public static final BlockEntry<AutoJewelCraftingTableBlock> AUTO_JEWEL_CRAFTING_TABLE_BLOCK = REGISTRATE
        .block("auto_jewel_crafting_table", AutoJewelCraftingTableBlock::new)
        .lang("Auto Jewel Crafting Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
        .recipe(RegistrumBlockRecipeLoader::autoJewelCraftingTableBlock)
        .register();

    public static final BlockEntry<AutoRoyalSmithingTableBlock> AUTO_ROYAL_SMITHING_TABLE_BLOCK = REGISTRATE
        .block("auto_royal_smithing_table", AutoRoyalSmithingTableBlock::new)
        .lang("Auto Royal Smithing Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.strength(5.0f, 1200f).noOcclusion())
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
        .recipe(RegistrumBlockRecipeLoader::autoRoyalSmithingTableBlock)
        .register();

    public static final BlockEntry<AutoRoyalGrindstoneBlock> AUTO_ROYAL_GRINDSTONE_BLOCK = REGISTRATE
        .block("auto_royal_grindstone", AutoRoyalGrindstoneBlock::new)
        .lang("Auto Royal Grindstone")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.strength(5.0f, 1200f).noOcclusion())
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
        .recipe(RegistrumBlockRecipeLoader::autoRoyalGrindstoneBlock)
        .register();

    public static final BlockEntry<AdjustablePowerConverterBlock> ADJUSTABLE_POWER_CONVERTER = REGISTRATE
        .block("adjustable_power_converter", AdjustablePowerConverterBlock::new)
        .lang("Adjustable Power Converter")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.lightLevel(state -> 9).noOcclusion().isValidSpawn(Blocks::never).emissiveRendering(ModBlocks::always))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .recipe(RegistrumBlockRecipeLoader::adjustablePowerConverter)
        .register();

    public static final BlockEntry<Block> CHAOTIC_RAW_ORE_BLOCK = REGISTRATE
        .block("chaotic_raw_ore_block", Block::new)
        .lang("Block of Chaotic Raw Ore")
        .initialProperties(() -> Blocks.RAW_IRON_BLOCK)
        .blockstate(DataGenUtil::noExtraModelOrState)
        .recipe(RegistrumBlockRecipeLoader::chaoticRawOreBlock)
        .item()
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.STORAGE_BLOCKS)
        .register();

    public static final BlockEntry<Block> DEEPSLATE_CHAOTIC_ORE = REGISTRATE
        .block("deepslate_chaotic_ore", Block::new)
        .initialProperties(() -> Blocks.DEEPSLATE_IRON_ORE)
        .item()
        .tag(Tags.Items.ORES)
        .build()
        .loot((tables, block) -> tables.add(block, tables.createOreDrop(block, AddonItems.CHAOTIC_RAW_ORE.get())))
        .tag(BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.ORES, Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
        .register();

    public static final BlockEntry<EnchantedGeneratorBlock> ENCHANTMENT_GENERATOR_BLOCK = REGISTRATE
        .block("enchanted_generator", EnchantedGeneratorBlock::new)
        .lang("Enchantment Generator")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.lightLevel(state -> 9).strength(5.0f, 1200f).noOcclusion().emissiveRendering(ModBlocks::always))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .simpleItem()
        .recipe(RegistrumBlockRecipeLoader::enchantmentGeneratorBlock)
        .register();

    public static final BlockEntry<ElectricEnchantingTableBlock> ELECTRIC_ENCHANTING_TABLE_BLOCK = REGISTRATE
        .block("electric_enchanting_table", ElectricEnchantingTableBlock::new)
        .lang("Electric Enchanting Table")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.noOcclusion().isValidSpawn(Blocks::never))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .properties(properties -> properties.sound(SoundType.WOOD))
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .recipe(RegistrumBlockRecipeLoader::electricEnchantingTableBlock)
        .register();

    public static final BlockEntry<EchoClusterBlock> ECHO_CLUSTER = REGISTRATE
        .block("echo_cluster", EchoClusterBlock::new)
        .lang("Echo Cluster")
        .initialProperties(() -> Blocks.AMETHYST_CLUSTER)
        .properties(p -> p.mapColor(MapColor.COLOR_CYAN).lightLevel((blockState) -> 5))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .loot((tables, block) -> tables.add(block, tables.createOreDrop(block, Items.ECHO_SHARD)))
        .item()
        .model(DataGenUtil::noExtraModelOrState)
        .build()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .register();

    public static final BlockEntry<BuddingEchoShardBlock> BUDDING_ECHO_SHARD = REGISTRATE
        .block("budding_echo_shard", BuddingEchoShardBlock::new)
        .lang("Budding Echo Shard")
        .initialProperties(() -> Blocks.BUDDING_AMETHYST)
        .properties(p -> p.mapColor(MapColor.COLOR_BLACK).strength(3f))
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .loot((tables, block) ->
            tables.add(block, tables.createSilkTouchDispatchTable(block, LootItem.lootTableItem(AddonItems.ECHO_GEODE.get()))))
        .register();

    public static final BlockEntry<SculkExtractorBlock> SCULK_EXTRACTOR = REGISTRATE
        .block("sculk_extractor", SculkExtractorBlock::new)
        .lang("Sculk Extractor")
        .initialProperties(() -> Blocks.SCULK)
        .properties(p -> p.mapColor(MapColor.COLOR_BLACK).strength(4.0F, 3.0F).sound(SoundType.SCULK_CATALYST).lightLevel((state) -> 9))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_HOE)
        .recipe(RegistrumBlockRecipeLoader::sculkExtractor)
        .register();

    public static final BlockEntry<VoidCatalystBlock> VOID_CATALYST = REGISTRATE
        .block("void_catalyst", VoidCatalystBlock::new)
        .lang("Void Catalyst")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(BlockBehaviour.Properties::randomTicks)
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .recipe(RegistrumBlockRecipeLoader::voidCatalyst)
        .register();

    public static final BlockEntry<ExperienceInterfaceBlock> EXPERIENCE_INTERFACE = REGISTRATE
        .block("experience_interface", ExperienceInterfaceBlock::new)
        .lang("Experience Interface")
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p.noOcclusion().isValidSpawn(Blocks::never))
        .blockstate(DataGenUtil::noExtraModelOrState)
        .simpleItem()
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .recipe(RegistrumBlockRecipeLoader::experienceInterface)
        .register();

    public static void register() {
    }
}