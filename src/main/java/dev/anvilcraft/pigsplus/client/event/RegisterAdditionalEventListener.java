package dev.anvilcraft.pigsplus.client.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = AnvilCraftPigsPlus.MOD_ID)
public class RegisterAdditionalEventListener {
    /**
     * 注册模型
     */
    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(AnvilCraftPigsPlus.of("block/enchanted_generator_head")));
    }
}
