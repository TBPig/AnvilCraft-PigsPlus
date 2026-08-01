package dev.anvilcraft.pigsplus.integration.jei.ingredient;

import com.mojang.serialization.Codec;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI 中表示天体改造需求/修改的概念 ingredient。
 */
public record ReformerConcept(ResourceLocation icon) {
    public static final IIngredientType<ReformerConcept> TYPE = () -> ReformerConcept.class;
    public static final Codec<ReformerConcept> CODEC =
        ResourceLocation.CODEC.xmap(ReformerConcept::new, ReformerConcept::icon);

    public String translationKey() {
        String path = this.icon.getPath();
        String name = path.substring(path.lastIndexOf('/') + 1);
        if (name.endsWith(".png")) {
            name = name.substring(0, name.length() - 4);
        }
        return "concept.anvilcraft_pigsplus." + name;
    }
}
