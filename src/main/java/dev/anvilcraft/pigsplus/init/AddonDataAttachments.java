package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.util.OceanEnchantmentData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class AddonDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AnvilCraftPigsPlus.MOD_ID);

    public static final Supplier<AttachmentType<OceanEnchantmentData>> OCEAN_ENCHANTMENT =
        ATTACHMENT_TYPES.register(
            "ocean_enchantment",
            () -> AttachmentType.builder(OceanEnchantmentData::empty)
                .serialize(OceanEnchantmentData.CODEC)
                .build()
        );

    private AddonDataAttachments() {
    }

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
