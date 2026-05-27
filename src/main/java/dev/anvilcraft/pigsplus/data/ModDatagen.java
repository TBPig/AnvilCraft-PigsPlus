package dev.anvilcraft.pigsplus.data;

import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.data.lang.LangHandler;
import dev.anvilcraft.pigsplus.data.recipe.RecipeHandler;
import net.neoforged.fml.common.EventBusSubscriber;

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
}