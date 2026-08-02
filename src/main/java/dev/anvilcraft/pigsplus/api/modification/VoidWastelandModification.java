package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceRecipe.WeightedEntry;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 将行星转换为虚空废土世界。
 */
public class VoidWastelandModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.void_wasteland");
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
            AnvilCraftPigsPlus.of("planet_resource/void_wasteland")
        );
        for (WeightedEntry entry : entries) {
            CelestialReformerPlanetUtil.addWastelandItem(resources, entry.resourceId(), entry.weight());
        }
    }
}
