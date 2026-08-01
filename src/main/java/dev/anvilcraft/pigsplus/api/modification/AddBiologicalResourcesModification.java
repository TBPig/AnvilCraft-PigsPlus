package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import net.minecraft.network.chat.Component;

/**
 * 按本体生物资源生成逻辑为行星添加生物资源。
 */
public class AddBiologicalResourcesModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.add_biological_resources");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        CelestialReformerPlanetUtil.addBiologicalResources(be);
    }
}
