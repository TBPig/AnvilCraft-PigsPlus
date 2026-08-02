package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 为行星添加低等文明与本体相同的文明资源，并停止生物资源产出。
 */
public class AddCivilizationModification extends ReformerModification {
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
        CelestialReformerPlanetUtil.addCivilization(be);
    }
}
