package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceRecipe;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetResourceRecipe.WeightedEntry;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet.WeightedItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 为行星添加低等文明与本体相同的文明资源，并停止生物资源产出。
 */
public class AddCivilizationModification extends ReformerModification {
    private static final List<ResourceLocation> CIVILIZATION_GEM_BLOCKS = List.of(
        ResourceLocation.withDefaultNamespace("emerald_block"),
        ResourceLocation.parse("anvilcraft:topaz_block"),
        ResourceLocation.parse("anvilcraft:ruby_block"),
        ResourceLocation.parse("anvilcraft:sapphire_block")
    );
    private static final List<ResourceLocation> CIVILIZATION_GEM_AMULETS = List.of(
        ResourceLocation.parse("anvilcraft:emerald_amulet"),
        ResourceLocation.parse("anvilcraft:topaz_amulet"),
        ResourceLocation.parse("anvilcraft:ruby_amulet"),
        ResourceLocation.parse("anvilcraft:sapphire_amulet")
    );

    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.add_civilization");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("civilization");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        PlanetaryResourceSet resources = CelestialReformerPlanetUtil.ensureResources(be);
        CelestialReformerPlanetUtil.listField(resources, "biologicalItems").clear();
        CelestialReformerPlanetUtil.listField(resources, "biologicalFluids").clear();
        CelestialReformerPlanetUtil.listField(resources, "wastelandItems").clear();
        CelestialReformerPlanetUtil.listField(resources, "offerings").clear();

        for (WeightedEntry entry : CelestialReformerPlanetUtil.getPlanetResourceEntries(
            be,
            PlanetResourceRecipe.Category.OFFERING
        )) {
            ResourceLocation id = entry.resourceId();
            if ("anvilcraft:gem_block_random".equals(id.toString())) {
                addOffering(resources, pickCivilizationResource(be, CIVILIZATION_GEM_BLOCKS), entry.weight());
            } else if ("anvilcraft:gem_amulet_random".equals(id.toString())) {
                addOffering(resources, pickCivilizationResource(be, CIVILIZATION_GEM_AMULETS), entry.weight());
            } else {
                addOffering(resources, id, entry.weight());
            }
        }
        CelestialReformerPlanetUtil.invokeVoid(resources, "setHasCivilization");
    }

    private static ResourceLocation pickCivilizationResource(
        CelestialForgingAnvilBlockEntity be,
        List<ResourceLocation> candidates
    ) {
        if (be.getLevel() == null) return candidates.getFirst();
        return candidates.get(be.getLevel().getRandom().nextInt(candidates.size()));
    }

    private static void addOffering(PlanetaryResourceSet resources, ResourceLocation item, int weight) {
        CelestialReformerPlanetUtil.invokeAdd(resources, "addOffering", new WeightedItemStack(item, weight));
    }
}
