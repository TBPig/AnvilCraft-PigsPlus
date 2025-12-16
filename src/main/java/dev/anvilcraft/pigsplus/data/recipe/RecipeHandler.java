package dev.anvilcraft.pigsplus.data.recipe;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.ItemInjectRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.wrap.TimeWarpRecipe;
import dev.dubhe.anvilcraft.recipe.mineral.MineralFountainRecipe;
import mezz.jei.api.constants.Tags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.BlockTagIngredient;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class RecipeHandler {
    public static void init(RegistrateRecipeProvider provider) {
        VanillaRecipesLoader.init(provider);
        SuperHeatingRecipeLoader.init(provider);
        MultipleToOneSmithingRecipeLoader.init(provider);


        MineralFountainRecipe.builder()
            .needBlock(AddonBlocks.CHAOTIC_RAW_ORE_BLOCK.get())
            .fromBlock(Blocks.DEEPSLATE)
            .toBlock(AddonBlocks.DEEPSLATE_CHAOTIC_ORE.get())
            .save(provider);

        ItemInjectRecipe.builder()
            .requires(Items.ECHO_SHARD)
            .inputBlock(Blocks.BUDDING_AMETHYST)
            .resultBlock(AddonBlocks.BUDDING_ECHO_SHARD)
            .save(provider);

        TimeWarpRecipe.builder()
            .requires(AddonItems.KARAKURI_COMPONENT)
            .requires(Items.ENDER_PEARL)
            .requires(Items.END_STONE,3)
            .result(AddonItems.ENDER_COMPONENT,0.2f)
            .save(provider);
    }
}
