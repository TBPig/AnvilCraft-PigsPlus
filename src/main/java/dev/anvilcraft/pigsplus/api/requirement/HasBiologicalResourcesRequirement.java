package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;

/**
 * 要求行星资源集中已生成生物资源。
 */
public class HasBiologicalResourcesRequirement extends ReformerRequirement {
    @Override
    public Component getDescription() {
        return this.text("requirement.anvilcraft_pigsplus.has_biological_resources");
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        var resources = be.getPlanetaryResourceSet();
        return resources != null
            && (!resources.getBiologicalItems().isEmpty() || !resources.getBiologicalFluids().isEmpty());
    }
}
