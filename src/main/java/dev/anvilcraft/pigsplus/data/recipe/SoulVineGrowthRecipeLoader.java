package dev.anvilcraft.pigsplus.data.recipe;

import dev.anvilcraft.lib.v2.registrum.providers.RegistrumRecipeProvider;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.recipe.SoulVineGrowthRecipe;
import dev.dubhe.anvilcraft.init.item.ModItemTags;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class SoulVineGrowthRecipeLoader {
    public static void init(RegistrumRecipeProvider provider) {
        SoulVineGrowthRecipe.builder()
            .requires(Tags.Items.BONES)
            .result(Items.BONE_MEAL, 9)
            .save(provider);
        SoulVineGrowthRecipe.builder()
            .requires(Tags.Items.BONES)
            .requires(Tags.Items.CROPS)
            .result(Items.BONE_MEAL, 18)
            .save(provider, "fixed_bone_meal");
        SoulVineGrowthRecipe.builder()
            .requires(ModItemTags.RESIN)
            .result(ModItems.HARDEND_RESIN, 3)
            .save(provider);
        SoulVineGrowthRecipe.builder()
            .requires(ModItemTags.RESIN)
            .fluid(Blocks.WATER_CAULDRON)
            .result(Items.SLIME_BALL, 3)
            .save(provider);
        SoulVineGrowthRecipe.builder()
            .requires(Items.SKELETON_SKULL)
            .requires(ItemTags.COALS, 2)
            .result(Items.WITHER_SKELETON_SKULL)
            .save(provider);
        SoulVineGrowthRecipe.builder()
            .requires(Items.ENDER_EYE)
            .requires(Items.GHAST_TEAR)
            .requires(Tags.Items.GLASS_BLOCKS_COLORLESS, 18)
            .result(Items.END_CRYSTAL, 4)
            .save(provider);
        SoulVineGrowthRecipe.builder()
            .requires(ModItems.SPONGE_GEMMULE, 4)
            .fluid(Blocks.WATER_CAULDRON)
            .consume(1000)
            .result(ModItems.SPONGE_GEMMULE, 5)
            .save(provider);
        SoulVineGrowthRecipe.builder()
            .requires(Tags.Items.GEMS_DIAMOND, 2)
            .requires(ModItemTags.TRANSCENDIUM_INGOTS)
            .result(AddonItems.SPIRITUAL_COMPONENT, 2)
            .save(provider);
    }
}
