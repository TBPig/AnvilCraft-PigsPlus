package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 按本体生物资源生成逻辑为行星添加生物资源。
 */
public class AddBiologicalResourcesModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.add_biological_resources");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("biologic");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (body == null || be.getLevel() == null) return;
        long randomOffset = be.getLevel().getRandom().nextLong();
        PlanetaryResourceSet generated = null;
        for (int i = 0; i < 64; i++) {
            PlanetaryResourceSet candidate = CelestialReformerPlanetUtil.generate(be, body, randomOffset + i);
            if (!candidate.getBiologicalItems().isEmpty() || !candidate.getBiologicalFluids().isEmpty()) {
                generated = candidate;
                break;
            }
        }
        if (generated == null) {
            generated = CelestialReformerPlanetUtil.generate(be, body, randomOffset);
        }
        PlanetaryResourceSet resources = CelestialReformerPlanetUtil.ensureResources(be);
        CelestialReformerPlanetUtil.listField(resources, "biologicalItems").addAll(generated.getBiologicalItems());
        CelestialReformerPlanetUtil.listField(resources, "biologicalFluids").addAll(generated.getBiologicalFluids());
        be.setPlanetaryResourceSet(resources);
    }
}
