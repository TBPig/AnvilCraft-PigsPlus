package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.MapCodec;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NoOtherMegastructureRequirement extends ReformerRequirement {
    @Override
    public Component getDescription() {
        return this.text("requirement.anvilcraft_pigsplus.no_other_megastructure");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("default");
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        return be.getActiveMegastructureIndex() < 0;
    }
}
