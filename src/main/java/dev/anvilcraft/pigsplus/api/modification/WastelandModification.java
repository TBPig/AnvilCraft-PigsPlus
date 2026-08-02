package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceRecipe.WeightedEntry;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 将行星转换为废土世界。
 */
public class WastelandModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.wasteland");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("plante_type");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        PlanetaryResourceSet resources = CelestialReformerPlanetUtil.resetWasteland(be);
        List<WeightedEntry> entries = CelestialReformerPlanetUtil.getWastelandEntries(
            be,
            ResourceLocation.fromNamespaceAndPath("anvilcraft", "planet_resource/wasteland")
        );
        for (WeightedEntry entry : entries) {
            CelestialReformerPlanetUtil.addWastelandItem(resources, entry.resourceId(), entry.weight());
        }
    }
}
