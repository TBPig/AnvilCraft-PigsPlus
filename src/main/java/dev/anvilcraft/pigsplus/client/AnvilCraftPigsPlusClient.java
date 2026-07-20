package dev.anvilcraft.pigsplus.client;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.markdown.recipe.MDPrecisionElectromagneticProcessingRecipeComponent;
import dev.anvilcraft.pigsplus.client.particle.ExpParticle;
import dev.anvilcraft.pigsplus.init.AddonParticleTypes;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.anvilcraft.resource.ageratum.client.feat.markdown.component.extend.MDRecipeComponent;
import dev.anvilcraft.resource.ageratum.client.registries.AgeratumRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
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

    public AnvilCraftPigsPlusClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterParticleProvidersEvent.class, event ->
            event.registerSpriteSet(AddonParticleTypes.EXP.get(), ExpParticle.Provider::new));
        RECIPE_COMPONENT_FACTORIES.register(modBus);
    }
}
