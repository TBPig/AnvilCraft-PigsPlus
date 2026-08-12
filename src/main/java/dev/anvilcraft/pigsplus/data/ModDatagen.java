package dev.anvilcraft.pigsplus.data;

import dev.anvilcraft.lib.v2.integration.IntegrationHook;
import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.data.advancement.AddonAdvancementHandler;
import dev.anvilcraft.pigsplus.data.lang.LangHandler;
import dev.anvilcraft.pigsplus.data.provider.AddonParticleDescriptionProvider;
import dev.anvilcraft.pigsplus.data.recipe.RecipeHandler;
import dev.anvilcraft.pigsplus.data.tags.BlockTagLoader;
import dev.anvilcraft.pigsplus.data.tags.EnchantmentTagLoader;
import dev.anvilcraft.pigsplus.data.tags.ItemTagLoader;
import dev.anvilcraft.pigsplus.init.enchantment.AddonEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class ModDatagen {
    /**
     * 初始化生成器
     */
    public static void init() {
        var genInit = REGISTRATE.getDataGenInitializer();
        genInit.add(Registries.ENCHANTMENT, AddonEnchantments::bootstrap);

        REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
        REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeHandler::init);
        REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, AddonAdvancementHandler::init);
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTagLoader::init);
        REGISTRATE.addDataGenerator(ProviderType.ENCHANTMENT_TAGS, EnchantmentTagLoader::init);
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, ItemTagLoader::init);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new AddonParticleDescriptionProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModCuriosProvider(packOutput, existingFileHelper, lookupProvider));

        IntegrationHook.setEvent(event);
    }
}
