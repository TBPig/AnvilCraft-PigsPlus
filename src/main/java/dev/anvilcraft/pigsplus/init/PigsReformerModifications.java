package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.api.modification.AddBiologicalResourcesModification;
import dev.anvilcraft.pigsplus.api.modification.AddCivilizationModification;
import dev.anvilcraft.pigsplus.api.modification.DecreaseTemperatureModification;
import dev.anvilcraft.pigsplus.api.modification.FastRotationModification;
import dev.anvilcraft.pigsplus.api.modification.IncreaseLiquidCoverageModification;
import dev.anvilcraft.pigsplus.api.modification.IncreaseTemperatureModification;
import dev.anvilcraft.pigsplus.api.modification.ReformerModification;
import dev.anvilcraft.pigsplus.api.modification.ReformerModifications;
import dev.anvilcraft.pigsplus.api.modification.SlowRotationModification;
import dev.anvilcraft.pigsplus.api.modification.StrengthenMagneticFieldModification;
import dev.anvilcraft.pigsplus.api.modification.VoidWastelandModification;
import dev.anvilcraft.pigsplus.api.modification.WastelandModification;
import dev.anvilcraft.pigsplus.api.modification.WeakenMagneticFieldModification;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PigsReformerModifications {
    private static final DeferredRegister<ReformerModification> DF =
        DeferredRegister.create(ReformerModifications.REGISTRY, AnvilCraftPigsPlus.MOD_ID);

    public static final DeferredHolder<ReformerModification, FastRotationModification> FAST_ROTATION =
        DF.register("fast_rotation", FastRotationModification::new);
    public static final DeferredHolder<ReformerModification, SlowRotationModification> SLOW_ROTATION =
        DF.register("slow_rotation", SlowRotationModification::new);
    public static final DeferredHolder<ReformerModification, StrengthenMagneticFieldModification> STRENGTHEN_MAGNETIC_FIELD =
        DF.register("strengthen_magnetic_field", StrengthenMagneticFieldModification::new);
    public static final DeferredHolder<ReformerModification, WeakenMagneticFieldModification> WEAKEN_MAGNETIC_FIELD =
        DF.register("weaken_magnetic_field", WeakenMagneticFieldModification::new);
    public static final DeferredHolder<ReformerModification, IncreaseLiquidCoverageModification> INCREASE_LIQUID_COVERAGE =
        DF.register("increase_liquid_coverage", IncreaseLiquidCoverageModification::new);
    public static final DeferredHolder<ReformerModification, IncreaseTemperatureModification> INCREASE_TEMPERATURE =
        DF.register("increase_temperature", IncreaseTemperatureModification::new);
    public static final DeferredHolder<ReformerModification, DecreaseTemperatureModification> DECREASE_TEMPERATURE =
        DF.register("decrease_temperature", DecreaseTemperatureModification::new);
    public static final DeferredHolder<ReformerModification, AddBiologicalResourcesModification> ADD_BIOLOGICAL_RESOURCES =
        DF.register("add_biological_resources", AddBiologicalResourcesModification::new);
    public static final DeferredHolder<ReformerModification, AddCivilizationModification> ADD_CIVILIZATION =
        DF.register("add_civilization", AddCivilizationModification::new);
    public static final DeferredHolder<ReformerModification, WastelandModification> WASTELAND =
        DF.register("wasteland", WastelandModification::new);
    public static final DeferredHolder<ReformerModification, VoidWastelandModification> VOID_WASTELAND =
        DF.register("void_wasteland", VoidWastelandModification::new);

    public static void register(IEventBus bus) {
        DF.register(bus);
    }
}
