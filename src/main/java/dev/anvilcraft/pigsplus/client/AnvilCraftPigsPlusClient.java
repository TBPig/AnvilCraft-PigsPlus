package dev.anvilcraft.pigsplus.client;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.markdown.recipe.MDPrecisionElectromagneticProcessingRecipeComponent;
import dev.anvilcraft.pigsplus.client.markdown.recipe.MDCelestialReformerRecipeComponent;
import dev.anvilcraft.pigsplus.client.particle.ExpParticle;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonFluids;
import dev.anvilcraft.pigsplus.init.AddonParticleTypes;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.dubhe.anvilcraft.util.ModClientFluidTypeExtensionImpl;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.component.extend.MDRecipeComponent;
import dev.anvilcraft.resource.ageratum.client.registries.AgeratumRegistries;
import net.minecraft.client.renderer.BiomeColors;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
@Mod(value = AnvilCraftPigsPlus.MOD_ID, dist = Dist.CLIENT)
public class AnvilCraftPigsPlusClient {
    public static final DeferredRegister<MDRecipeComponent.RecipeComponentFactory<?>>
        RECIPE_COMPONENT_FACTORIES = DeferredRegister.create(
            AgeratumRegistries.RECIPE_COMPONENT_FACTORY_REGISTRY_KEY, AnvilCraftPigsPlus.MOD_ID);

    public static final DeferredHolder<MDRecipeComponent.RecipeComponentFactory<?>, MDRecipeComponent.RecipeComponentFactory<?>>
        PRECISION_ELECTROMAGNETIC_PROCESSING = RECIPE_COMPONENT_FACTORIES.register(
            "precision_electromagnetic_processing", () -> MDRecipeComponent.RecipeComponentFactory.create(
                AddonRecipeTypes.PRECISION_ELECTROMAGNETIC_PROCESSING_TYPE.get(),
                MDPrecisionElectromagneticProcessingRecipeComponent::new
            )
        );

    public static final DeferredHolder<MDRecipeComponent.RecipeComponentFactory<?>, MDRecipeComponent.RecipeComponentFactory<?>>
        CELESTIAL_REFORMER = RECIPE_COMPONENT_FACTORIES.register(
            "celestial_reformer", () -> MDRecipeComponent.RecipeComponentFactory.create(
                AddonRecipeTypes.CELESTIAL_REFORMER_TYPE.get(),
                MDCelestialReformerRecipeComponent::new
            )
        );

    public AnvilCraftPigsPlusClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterParticleProvidersEvent.class, event ->
            event.registerSpriteSet(AddonParticleTypes.EXP.get(), ExpParticle.Provider::new));
        modBus.addListener(AnvilCraftPigsPlusClient::onRegisterFluidType);
        modBus.addListener(AnvilCraftPigsPlusClient::onRegisterBlockColors);
        modBus.addListener(AnvilCraftPigsPlusClient::onRegisterItemColors);
        RECIPE_COMPONENT_FACTORIES.register(modBus);
    }

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
            (state, level, pos, tintIndex) ->
                level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1,
            AddonBlocks.BRASS_SINK.get()
        );
    }

    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
            (stack, tintIndex) -> tintIndex == 0 ? 0x3F76E4 : -1,
            AddonBlocks.BRASS_SINK.asItem()
        );
    }

    public static void onRegisterFluidType(RegisterClientExtensionsEvent e) {
        e.registerFluidType(
            new ModClientFluidTypeExtensionImpl(
                AnvilCraftPigsPlus.of("block/void_acid"),
                AnvilCraftPigsPlus.of("block/void_acid_flow"),
                0x1A0033,
                0.8f,
                0xFFFFFFFF,
                false
            ), AddonFluids.VOID_ACID_TYPE
        );
    }
}
