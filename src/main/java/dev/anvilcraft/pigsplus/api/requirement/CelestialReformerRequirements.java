package dev.anvilcraft.pigsplus.api.requirement;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public final class CelestialReformerRequirements {
    public static final ResourceKey<Registry<ReformerRequirement>> KEY =
        ResourceKey.createRegistryKey(AnvilCraftPigsPlus.of("celestial_reformer_requirement"));
    public static final Registry<ReformerRequirement> REGISTRY = new RegistryBuilder<>(KEY)
        .sync(true)
        .maxId(512)
        .create();

    private CelestialReformerRequirements() {
    }

    @SubscribeEvent
    public static void register(NewRegistryEvent event) {
        event.register(REGISTRY);
    }
}
