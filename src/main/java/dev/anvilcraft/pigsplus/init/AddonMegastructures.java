package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.megastructure.PlanetaryReformerHandler;
import dev.anvilcraft.pigsplus.block.entity.megastructure.StarReformerHandler;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialRefactorRegistry;
import dev.dubhe.anvilcraft.block.entity.celestial.GiantPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.Megastructure;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.StarData;
import dev.dubhe.anvilcraft.init.registry.ModRegistryKeys;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class AddonMegastructures {
    private static final DeferredRegister<Megastructure> REGISTER = DeferredRegister.create(
        ModRegistryKeys.MEGASTRUCTURE,
        AnvilCraftPigsPlus.MOD_ID
    );


    public static final DeferredHolder<Megastructure, Megastructure> PLANETARY_REFORMER = register(
        PlanetaryReformerHandler.NAME,
        id -> Megastructure.builder(id, PlanetaryReformerHandler.NAME)
            .prerequisite(AddonMegastructures::isPlanet)
            .ring(AddonMegastructures::getInnermostRing)
            .model(1, AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_1_reformer"))
            .model(2, AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_1_reformer"))
            .model(4, AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_4_reformer"))
            .model(5, AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_4_reformer"))
            .material(AddonItems.CELESTIAL_REFORMER_COMPONENT.asItem(), 16)
            .handler(PlanetaryReformerHandler::new)
            .build()
    );

    public static final DeferredHolder<Megastructure, Megastructure> STAR_REFORMER = register(
        StarReformerHandler.NAME,
        id -> Megastructure.builder(id, StarReformerHandler.NAME)
            .prerequisite(context -> context.body() instanceof StarData)
            .ring(AddonMegastructures::getInnermostRing)
            .model(2, AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_4_reformer"))
            .model(4, AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_4_reformer"))
            .model(5, AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_4_reformer"))
            .material(AddonItems.CELESTIAL_REFORMER_COMPONENT.asItem(), 64)
            .handler(StarReformerHandler::new)
            .build()
    );

    private AddonMegastructures() {
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    private static DeferredHolder<Megastructure, Megastructure> register(
        String name,
        Function<ResourceLocation, Megastructure> factory
    ) {
        ResourceLocation id = AnvilCraftPigsPlus.of(name);
        return REGISTER.register(name, () -> factory.apply(id));
    }

    private static int getInnermostRing(Megastructure.Context context) {
        return CelestialRefactorRegistry.getInnermostRing(context.body(), context.amplified());
    }

    private static boolean isPlanet(Megastructure.Context context) {
        return context.body() instanceof RockyPlanetData
            || context.body() instanceof GiantPlanetData
            || (context.body() instanceof SpecialCelestialBodyData special && !special.isErrorPlanet());
    }
}
