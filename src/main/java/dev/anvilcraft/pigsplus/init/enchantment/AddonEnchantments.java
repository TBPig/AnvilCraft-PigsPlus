package dev.anvilcraft.pigsplus.init.enchantment;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.enchantment.SqrtIncreaseValue;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

public class AddonEnchantments {
    public static final ResourceKey<Enchantment> ENDURANCE = key("endurance");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, AnvilCraftPigsPlus.of(name));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> itemHolderGetter = context.lookup(Registries.ITEM);
        register(
            context,
            ENDURANCE,
            Enchantment.enchantment(
                Enchantment.definition(
                    itemHolderGetter.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                    5,
                    4,
                    Enchantment.dynamicCost(5, 10),
                    Enchantment.dynamicCost(41, 10),
                    2,
                    EquipmentSlotGroup.ARMOR
                )
            ).withEffect(
                EnchantmentEffectComponents.ATTRIBUTES,
                new EnchantmentAttributeEffect(
                    AnvilCraftPigsPlus.of("endurance_max_health"),
                    Attributes.MAX_HEALTH,
                    new SqrtIncreaseValue(2.0f),
                    AttributeModifier.Operation.ADD_VALUE
                )
            )
        );
    }

    public static void register(
        BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
}
