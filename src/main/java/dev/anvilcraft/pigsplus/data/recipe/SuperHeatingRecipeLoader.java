package dev.anvilcraft.pigsplus.data.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.SuperHeatingRecipe;
import net.minecraft.world.item.Items;

public class SuperHeatingRecipeLoader {
    public static void init(RegistrateRecipeProvider provider) {
        SuperHeatingRecipe.builder()
            .requires(AddonItems.CHAOTIC_RAW_ORE)
            .result(Items.COPPER_INGOT, 0.3f)
            .result(Items.GOLD_INGOT, 0.3f)
            .result(ModItems.ZINC_INGOT, 0.3f)
            .result(ModItems.TIN_INGOT, 0.3f)
            .result(ModItems.TITANIUM_INGOT, 0.3f)
            .result(ModItems.TUNGSTEN_INGOT, 0.3f)
            .result(ModItems.LEAD_INGOT, 0.3f)
            .result(ModItems.SILVER_INGOT, 0.3f)
            .save(provider);
    }
}
