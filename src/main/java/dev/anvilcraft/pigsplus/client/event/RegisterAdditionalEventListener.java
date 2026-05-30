package dev.anvilcraft.pigsplus.client.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.renderer.blockentity.AdjustablePowerConverterRenderer;
import dev.anvilcraft.pigsplus.client.renderer.blockentity.EnchantedGeneratorRenderer;
import dev.anvilcraft.pigsplus.client.renderer.blockentity.ExperienceInterfaceRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;

@EventBusSubscriber(value = Dist.CLIENT, modid = AnvilCraftPigsPlus.MOD_ID)
public class RegisterAdditionalEventListener {
    /**
     * 注册模型
     */
    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterStandalone event) {
        event.register(
            AdjustablePowerConverterRenderer.MODEL,
            SimpleUnbakedStandaloneModel.blockStateModel(AnvilCraftPigsPlus.of("block/adjustable_power_converter_core"))
        );
        event.register(
            EnchantedGeneratorRenderer.MODEL,
            SimpleUnbakedStandaloneModel.blockStateModel(AnvilCraftPigsPlus.of("block/enchanted_generator_head"))
        );
        event.register(
            ExperienceInterfaceRenderer.MODEL,
            SimpleUnbakedStandaloneModel.blockStateModel(AnvilCraftPigsPlus.of("block/experience_interface_head"))
        );
    }
}
