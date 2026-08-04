package dev.anvilcraft.pigsplus;

import com.mojang.logging.LogUtils;
import dev.anvilcraft.lib.v2.config.ConfigManager;
import dev.anvilcraft.lib.v2.network.register.NetworkRegistrar;
import dev.anvilcraft.lib.v2.registrum.Registrum;
import dev.anvilcraft.pigsplus.config.AddonServerConfig;
import dev.anvilcraft.pigsplus.data.ModDatagen;
import dev.anvilcraft.pigsplus.event.CapabilitiesEventListener;
import dev.anvilcraft.pigsplus.init.AddonBlockEntities;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonCriterionTriggers;
import dev.anvilcraft.pigsplus.init.AddonDataComponents;
import dev.anvilcraft.pigsplus.init.PigsReformerRequirements;
import dev.anvilcraft.pigsplus.init.AddonFluids;
import dev.anvilcraft.pigsplus.init.AddonInteractionMaps;
import dev.anvilcraft.pigsplus.init.AddonItemGroups;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.init.AddonParticleTypes;
import dev.anvilcraft.pigsplus.init.PigsReformerModifications;
import dev.anvilcraft.pigsplus.init.AddonRecipeTypes;
import dev.anvilcraft.pigsplus.init.enchantment.AddonEnchantmentLevelBasedValueTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(AnvilCraftPigsPlus.MOD_ID)
public class AnvilCraftPigsPlus {
    public static final String MOD_ID = "anvilcraft_pigsplus";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AddonServerConfig CONFIG = ConfigManager.register(AnvilCraftPigsPlus.MOD_ID, AddonServerConfig::new);
    public static final Registrum REGISTRATE = Registrum.create(MOD_ID);

    public AnvilCraftPigsPlus(IEventBus modEventBus, ModContainer modContainer) {
        AddonFluids.register(modEventBus);
        AddonItemGroups.register(modEventBus);
        AddonCriterionTriggers.register(modEventBus);
        AddonDataComponents.register(modEventBus);
        AddonBlocks.register();
        AddonItems.register();
        AddonMenuTypes.register();
        AddonParticleTypes.register(modEventBus);
        AddonBlockEntities.register();
        modEventBus.addListener(CapabilitiesEventListener::registerCapabilities);
        AddonRecipeTypes.register(modEventBus);
        PigsReformerRequirements.register(modEventBus);
        PigsReformerModifications.register(modEventBus);
        AddonEnchantmentLevelBasedValueTypes.register(modEventBus);
        ModDatagen.init();
        modEventBus.addListener(AnvilCraftPigsPlus::commonSetup);

        modEventBus.addListener(AnvilCraftPigsPlus::registerPayload);
    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void registerPayload(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        NetworkRegistrar.register(registrar, AnvilCraftPigsPlus.MOD_ID);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(AddonInteractionMaps::init);
    }
}
