package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.recipe.CelestialReformerRecipe;
import dev.anvilcraft.pigsplus.recipe.PrecisionElectromagneticProcessingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(Registries.RECIPE_TYPE, AnvilCraftPigsPlus.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, AnvilCraftPigsPlus.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<PrecisionElectromagneticProcessingRecipe>> PRECISION_ELECTROMAGNETIC_PROCESSING_TYPE =
        registerType("precision_electromagnetic_processing");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PrecisionElectromagneticProcessingRecipe>> PRECISION_ELECTROMAGNETIC_PROCESSING_SERIALIZER =
        RECIPE_SERIALIZERS.register("precision_electromagnetic_processing", PrecisionElectromagneticProcessingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CelestialReformerRecipe>> CELESTIAL_REFORMER_TYPE =
        registerType("celestial_reformer");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CelestialReformerRecipe>> CELESTIAL_REFORMER_SERIALIZER =
        RECIPE_SERIALIZERS.register("celestial_reformer", CelestialReformerRecipe.Serializer::new);

    private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerType(String name) {
        return RECIPE_TYPES.register(
            name, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return AnvilCraftPigsPlus.of(name).toString();
                }
            }
        );
    }

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
