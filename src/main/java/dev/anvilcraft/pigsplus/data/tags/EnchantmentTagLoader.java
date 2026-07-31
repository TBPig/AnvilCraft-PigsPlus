package dev.anvilcraft.pigsplus.data.tags;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumTagsProvider;
import dev.anvilcraft.pigsplus.init.enchantment.AddonEnchantments;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentTagLoader {
    public static void init(RegistrumTagsProvider<Enchantment> provider) {
        provider.addTag(EnchantmentTags.IN_ENCHANTING_TABLE)
            .addOptional(AddonEnchantments.ENDURANCE.location());

        provider.addTag(EnchantmentTags.NON_TREASURE)
            .addOptional(AddonEnchantments.ENDURANCE.location());

        provider.addTag(EnchantmentTags.ON_RANDOM_LOOT)
            .addOptional(AddonEnchantments.ENDURANCE.location());
    }
}
