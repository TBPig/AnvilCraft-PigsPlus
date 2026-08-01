package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
        CelestialReformerPlanetUtil.setWasteland(be, true);
    }
}
