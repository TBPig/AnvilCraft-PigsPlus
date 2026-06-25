package dev.anvilcraft.pigsplus.data;

import dev.anvilcraft.lib.v2.integration.IntegrationHook;
import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.data.lang.LangHandler;
import dev.anvilcraft.pigsplus.data.provider.AddonParticleDescriptionProvider;
import dev.anvilcraft.pigsplus.data.recipe.RecipeHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class ModDatagen {
    /**
     * 初始化生成器
     */
    public static void init() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
        REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeHandler::init);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new AddonParticleDescriptionProvider(packOutput, existingFileHelper));

        IntegrationHook.setEvent(event);
    }
}