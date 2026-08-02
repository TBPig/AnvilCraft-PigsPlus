package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import dev.anvilcraft.pigsplus.block.entity.megastructure.PlanetaryReformerHandler;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PlanetaryReformerRequirement extends ReformerRequirement {
    @Override
    public Component getDescription() {
        return this.text("requirement.anvilcraft_pigsplus.planetary_reformer");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("reformer");
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        return be.getActiveMegastructureOption() != null
            && PlanetaryReformerHandler.NAME.equals(be.getActiveMegastructureOption().megastructure());
    }
}
