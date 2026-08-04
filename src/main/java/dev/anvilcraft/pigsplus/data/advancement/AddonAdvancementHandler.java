package dev.anvilcraft.pigsplus.data.advancement;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumAdvancementProvider;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.advancement.PigAnvilTransformTrigger;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class AddonAdvancementHandler {
    public static final AdvancementHolder ROOT = Advancement.Builder.advancement()
        .display(
            AddonBlocks.PIG_ANVIL,
            Component.translatable("advancements.anvilcraft_pigsplus.root.title"),
            Component.translatable("advancements.anvilcraft_pigsplus.root.description"),
            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png"),
            AdvancementType.TASK,
            false,
            true,
            false
        )
        .addCriterion("join", PlayerTrigger.TriggerInstance.tick())
        .build(AnvilCraftPigsPlus.of("root"));

    public static final AdvancementHolder PIG_ANVIL_ONE = Advancement.Builder.advancement()
        .parent(ROOT)
        .display(
            AddonBlocks.PIG_ANVIL,
            Component.translatable("advancements.anvilcraft_pigsplus.pig_anvil_one.title"),
            Component.translatable("advancements.anvilcraft_pigsplus.pig_anvil_one.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
        )
        .addCriterion("pig_anvil_transform", PigAnvilTransformTrigger.TriggerInstance.singlePig())
        .build(AnvilCraftPigsPlus.of("pig_anvil_one"));

    public static final AdvancementHolder TRIPLE_BIG_PIG = Advancement.Builder.advancement()
        .parent(PIG_ANVIL_ONE)
        .display(
            Items.COOKED_PORKCHOP,
            Component.translatable("advancements.anvilcraft_pigsplus.triple_big_pig.title"),
            Component.translatable("advancements.anvilcraft_pigsplus.triple_big_pig.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            true
        )
        .addCriterion("triple_big_pig", PigAnvilTransformTrigger.TriggerInstance.triplePig())
        .build(AnvilCraftPigsPlus.of("triple_big_pig"));

    public static final AdvancementHolder KARAKURI_COMPONENT = Advancement.Builder.advancement()
        .parent(ROOT)
        .display(
            AddonItems.KARAKURI_COMPONENT,
            Component.translatable("advancements.anvilcraft_pigsplus.karakuri_component.title"),
            Component.translatable("advancements.anvilcraft_pigsplus.karakuri_component.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
        )
        .addCriterion("has_karakuri_component", InventoryChangeTrigger.TriggerInstance.hasItems(AddonItems.KARAKURI_COMPONENT))
        .build(AnvilCraftPigsPlus.of("karakuri_component"));

    public static final AdvancementHolder AUTO_MACHINE = Advancement.Builder.advancement()
        .parent(KARAKURI_COMPONENT)
        .display(
            AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK,
            Component.translatable("advancements.anvilcraft_pigsplus.auto_machine.title"),
            Component.translatable("advancements.anvilcraft_pigsplus.auto_machine.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
        )
        .requirements(AdvancementRequirements.Strategy.OR)
        .addCriterion(
            "has_auto_jewel_crafting_table",
            InventoryChangeTrigger.TriggerInstance.hasItems(AddonBlocks.AUTO_JEWEL_CRAFTING_TABLE_BLOCK)
        )
        .addCriterion(
            "has_auto_royal_smithing_table",
            InventoryChangeTrigger.TriggerInstance.hasItems(AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK)
        )
        .addCriterion(
            "has_auto_royal_grindstone",
            InventoryChangeTrigger.TriggerInstance.hasItems(AddonBlocks.AUTO_ROYAL_GRINDSTONE_BLOCK)
        )
        .build(AnvilCraftPigsPlus.of("auto_machine"));

    public static final AdvancementHolder SPIRITUAL_COMPONENT = Advancement.Builder.advancement()
        .parent(KARAKURI_COMPONENT)
        .display(
            AddonItems.SPIRITUAL_COMPONENT,
            Component.translatable("advancements.anvilcraft_pigsplus.spiritual_component.title"),
            Component.translatable("advancements.anvilcraft_pigsplus.spiritual_component.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
        )
        .addCriterion("has_spiritual_component", InventoryChangeTrigger.TriggerInstance.hasItems(AddonItems.SPIRITUAL_COMPONENT))
        .build(AnvilCraftPigsPlus.of("spiritual_component"));

    public static final AdvancementHolder ELECTRIC_ENCHANTING_TABLE = Advancement.Builder.advancement()
        .parent(SPIRITUAL_COMPONENT)
        .display(
            AddonBlocks.ELECTRIC_ENCHANTING_TABLE_BLOCK,
            Component.translatable("advancements.anvilcraft_pigsplus.electric_enchanting_table.title"),
            Component.translatable("advancements.anvilcraft_pigsplus.electric_enchanting_table.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
        )
        .addCriterion(
            "has_electric_enchanting_table",
            InventoryChangeTrigger.TriggerInstance.hasItems(AddonBlocks.ELECTRIC_ENCHANTING_TABLE_BLOCK)
        )
        .build(AnvilCraftPigsPlus.of("electric_enchanting_table"));

    public static void init(RegistrumAdvancementProvider provider) {
        provider.accept(ROOT);
        provider.accept(PIG_ANVIL_ONE);
        provider.accept(TRIPLE_BIG_PIG);
        provider.accept(KARAKURI_COMPONENT);
        provider.accept(AUTO_MACHINE);
        provider.accept(SPIRITUAL_COMPONENT);
        provider.accept(ELECTRIC_ENCHANTING_TABLE);
    }
}
