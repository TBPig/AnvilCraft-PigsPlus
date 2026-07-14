package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.anvil.AutoChickenBehavior;
import dev.anvilcraft.pigsplus.anvil.BlockBreakerBehavior;
import dev.anvilcraft.pigsplus.anvil.CursedGoldBehavior;
import dev.anvilcraft.pigsplus.anvil.VoidCatalystBehavior;
import dev.dubhe.anvilcraft.api.event.AnvilBehaviorRegisterEvent;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class ModAnvilBehaviors {
    @SubscribeEvent
    public static void register(AnvilBehaviorRegisterEvent event) {
        event.registerBehavior(AddonBlocks.BLOCK_BREAKER.get(), new BlockBreakerBehavior());
        event.registerBehavior(AddonBlocks.AUTO_CHICKEN.get(), new AutoChickenBehavior());
        event.registerBehavior(AddonBlocks.VOID_CATALYST.get(), new VoidCatalystBehavior());
        event.registerBehavior(ModBlocks.CURSED_GOLD_BLOCK.get(), new CursedGoldBehavior());
    }
}
