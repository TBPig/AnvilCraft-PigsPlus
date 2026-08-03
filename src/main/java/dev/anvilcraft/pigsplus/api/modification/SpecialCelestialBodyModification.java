package dev.anvilcraft.pigsplus.api.modification;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SpecialCelestialBodyModification extends ReformerModification {
    public static final MapCodec<SpecialCelestialBodyModification> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("recipe")
                .forGetter(SpecialCelestialBodyModification::getRecipeId)
        ).apply(instance, SpecialCelestialBodyModification::new));

    private final ResourceLocation recipeId;

    public SpecialCelestialBodyModification() {
        this(AnvilCraftPigsPlus.of("special_celestial_body/error_planet"));
    }

    public SpecialCelestialBodyModification(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public ResourceLocation getRecipeId() {
        return this.recipeId;
    }

    @Override
    public MapCodec<? extends ReformerModification> codec() {
        return CODEC;
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        if (be.getLevel() == null) return;
        RecipeHolder<?> holder = be.getLevel().getRecipeManager().byKey(this.recipeId).orElse(null);
        if (holder == null || !(holder.value() instanceof SpecialCelestialBodyRecipe recipe)) {
            AnvilCraftPigsPlus.LOGGER.warn("Missing special celestial body recipe: {}", this.recipeId);
            return;
        }
        be.setCelestialBodyData(SpecialCelestialBodyData.fromRecipe(recipe, this.recipeId.toString()));
        be.setPlanetaryResourceSet(recipe.generateResources());
    }

    @Override
    public Component getDescription() {
        String path = this.recipeId.getPath();
        String name = path.substring(path.lastIndexOf('/') + 1);
        Component planetName = Component.translatable("screen.anvilcraft.cfa.class.special." + name);
        return this.text("modification.anvilcraft_pigsplus.special_celestial_body", planetName);
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("plante_type");
    }
}
