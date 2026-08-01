package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public final class ReformerModifications {
    public static final ResourceKey<Registry<ReformerModification>> KEY =
        ResourceKey.createRegistryKey(AnvilCraftPigsPlus.of("celestial_reformer_modification"));
    public static final Registry<ReformerModification> REGISTRY = new RegistryBuilder<>(KEY)
        .sync(true)
        .maxId(512)
        .create();

    private ReformerModifications() {
    }

    @SubscribeEvent
    public static void register(NewRegistryEvent event) {
        event.register(REGISTRY);
    }
}
