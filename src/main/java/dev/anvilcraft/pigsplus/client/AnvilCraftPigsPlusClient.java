package dev.anvilcraft.pigsplus.client;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.particle.ExpParticle;
import dev.anvilcraft.pigsplus.client.renderer.entity.StalkerModel;
import dev.anvilcraft.pigsplus.init.AddonParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@Mod(value = AnvilCraftPigsPlus.MOD_ID, dist = Dist.CLIENT)
public class AnvilCraftPigsPlusClient {
    public AnvilCraftPigsPlusClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(
            RegisterParticleProvidersEvent.class, event ->
                event.registerSpriteSet(AddonParticleTypes.EXP.get(), ExpParticle.Provider::new)
        );

        modBus.addListener(
            EntityRenderersEvent.RegisterLayerDefinitions.class, event ->
                event.registerLayerDefinition(StalkerModel.LAYER_LOCATION, StalkerModel::createBodyLayer)
        );
    }
}
