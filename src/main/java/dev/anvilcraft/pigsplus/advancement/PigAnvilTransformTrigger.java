package dev.anvilcraft.pigsplus.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.pigsplus.init.AddonCriterionTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PigAnvilTransformTrigger extends SimpleCriterionTrigger<PigAnvilTransformTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, boolean triple) {
        this.trigger(player, instance -> instance.matches(triple));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Boolean> triple)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            Codec.BOOL.optionalFieldOf("triple").forGetter(TriggerInstance::triple)
        ).apply(instance, TriggerInstance::new));

        public static Criterion<TriggerInstance> singlePig() {
            return pigAnvilTransform(false);
        }

        public static Criterion<TriggerInstance> triplePig() {
            return pigAnvilTransform(true);
        }

        private static Criterion<TriggerInstance> pigAnvilTransform(boolean triple) {
            return AddonCriterionTriggers.PIG_ANVIL_TRANSFORM.get().createCriterion(
                new TriggerInstance(Optional.empty(), Optional.of(triple))
            );
        }

        public boolean matches(boolean triple) {
            return this.triple.isEmpty() || this.triple.get() == triple;
        }
    }
}
