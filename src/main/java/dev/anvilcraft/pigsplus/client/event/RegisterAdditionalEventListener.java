package dev.anvilcraft.pigsplus.client.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = AnvilCraftPigsPlus.MOD_ID)
public class RegisterAdditionalEventListener {
    /**
     * 注册模型
     */
    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(AnvilCraftPigsPlus.of("block/enchanted_generator_head")));
        event.register(ModelResourceLocation.standalone(AnvilCraftPigsPlus.of("block/adjustable_power_converter_core")));
        event.register(ModelResourceLocation.standalone(AnvilCraftPigsPlus.of("block/adjustable_power_converter_core_out")));
        event.register(ModelResourceLocation.standalone(AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_1_reformer")));
        event.register(ModelResourceLocation.standalone(AnvilCraftPigsPlus.of("block/celestial_forging_anvil_ring_4_reformer")));
    }
}
