package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.lib.v2.registrum.util.CreativeTabSection;
import dev.anvilcraft.lib.v2.registrum.util.CreativeTabSections;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public final class AddonItemTabContents {
    private AddonItemTabContents() {
    }

    public static void addItems(
        CreativeModeTab.ItemDisplayParameters parameters,
        CreativeModeTab.Output output
    ) {
        CreativeTabSections.build(
            AnvilCraftPigsPlus.of("addon_items"),
            parameters,
            output,
            sections -> {
                sections.section(
                    createSection(
                        "items/tools",
                        "itemGroup.anvilcraft_pigsplus.section.tools"
                    ),
                    AddonItemTabContents::addTools
                );
                sections.section(
                    createSection(
                        "items/power",
                        "itemGroup.anvilcraft_pigsplus.section.fe_compatibility"
                    ),
                    AddonItemTabContents::addFeCompatibility
                );
                sections.section(
                    createSection(
                        "functional_blocks/redstone",
                        "itemGroup.anvilcraft_pigsplus.section.redstone"
                    ),
                    AddonItemTabContents::addRedstone
                );
                sections.section(
                    createSection(
                        "items/produced",
                        "itemGroup.anvilcraft_pigsplus.section.automation"
                    ),
                    AddonItemTabContents::addAutomation
                );
                sections.section(
                    createSection(
                        "items/materials",
                        "itemGroup.anvilcraft_pigsplus.section.materials"
                    ),
                    AddonItemTabContents::addMaterials
                );
            }
        );
    }

    private static CreativeTabSection createSection(String texturePath, String titleKey) {
        return CreativeTabSection.builder(
                ResourceLocation.fromNamespaceAndPath(
                    "anvilcraft",
                    "textures/gui/creative_inventory/section/" + texturePath + ".png"
                )
            )
            .text(Component.translatable(titleKey))
            .build();
    }

    private static void addTools(CreativeModeTab.Output output) {
        output.accept(AddonBlocks.PIG_ANVIL);
        output.accept(AddonBlocks.WEAK_RESIN_BLOCK);
        output.accept(AddonItems.MENGER_SPONGE_STAFF);
        output.accept(AddonItems.BLOCK_BREAKER_STAFF);
        output.accept(AddonItems.MENGER_SPONGE_HOLY_STAFF);
    }

    private static void addFeCompatibility(CreativeModeTab.Output output) {
        output.accept(AddonBlocks.ADJUSTABLE_POWER_CONVERTER);
        output.accept(AddonItems.PORTABLE_WIRELESS_CHARGER);
        output.accept(AddonItems.GRID_ADAPTER);
    }

    private static void addRedstone(CreativeModeTab.Output output) {
        output.accept(AddonBlocks.REDSTONE_CONDUIT_BLOCK);
        output.accept(AddonBlocks.MEMORY_BLOCK_COMPARATOR);
    }

    private static void addAutomation(CreativeModeTab.Output output) {
        output.accept(AddonBlocks.VOID_CATALYST);
        output.accept(AddonBlocks.EXPERIENCE_INTERFACE);
        output.accept(AddonBlocks.BRASS_SINK);
        output.accept(AddonBlocks.AUTO_CHICKEN);
        output.accept(AddonBlocks.BLOCK_BREAKER);
        output.accept(AddonBlocks.PRECISION_MAGNETIC_PIVOT);
        output.accept(AddonBlocks.CHAIN_SMITHING_TABLE_BLOCK);
        output.accept(AddonBlocks.AUTO_JEWEL_CRAFTING_TABLE_BLOCK);
        output.accept(AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK);
        output.accept(AddonBlocks.AUTO_ROYAL_GRINDSTONE_BLOCK);
        output.accept(AddonBlocks.WIRELESS_TRANSMITTER);
        output.accept(AddonBlocks.ENCHANTMENT_GENERATOR_BLOCK);
        output.accept(AddonBlocks.ELECTRIC_ENCHANTING_TABLE_BLOCK);
    }

    private static void addMaterials(CreativeModeTab.Output output) {
        output.accept(AddonItems.KARAKURI_COMPONENT);
        output.accept(AddonItems.SPIRITUAL_COMPONENT);
        output.accept(AddonItems.ENDER_COMPONENT);
        output.accept(AddonItems.UNIVERSAL_REDSTONE_COMPONENT);
        output.accept(AddonItems.ENDER_SEED);
        output.accept(AddonItems.ECHO_GEODE);
        output.accept(AddonItems.CELESTIAL_REFORMER_COMPONENT);
        output.accept(AddonItems.CHAOTIC_RAW_ORE);
        output.accept(AddonItems.VOID_ACID_BUCKET);
        output.accept(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK);
        output.accept(AddonBlocks.DEEPSLATE_CHAOTIC_ORE);
        output.accept(AddonBlocks.ECHO_CLUSTER);
        output.accept(AddonBlocks.BUDDING_ECHO_SHARD);
    }
}
