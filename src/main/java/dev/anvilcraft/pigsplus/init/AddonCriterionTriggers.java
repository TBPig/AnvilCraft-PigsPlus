package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.advancement.PigAnvilTransformTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonCriterionTriggers {
    private static final DeferredRegister<CriterionTrigger<?>> REGISTER =
        DeferredRegister.create(Registries.TRIGGER_TYPE, AnvilCraftPigsPlus.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, PigAnvilTransformTrigger> PIG_ANVIL_TRANSFORM =
        REGISTER.register("pig_anvil_transform", PigAnvilTransformTrigger::new);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
