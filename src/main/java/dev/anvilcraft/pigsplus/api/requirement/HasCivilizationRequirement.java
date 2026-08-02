package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 要求行星资源集已生成文明。
 */
public class HasCivilizationRequirement extends ReformerRequirement {
    @Override
    public Component getDescription() {
        return this.text("requirement.anvilcraft_pigsplus.has_civilization");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("civilization");
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        return be.getPlanetaryResourceSet() != null && be.getPlanetaryResourceSet().hasCivilization();
    }
}
