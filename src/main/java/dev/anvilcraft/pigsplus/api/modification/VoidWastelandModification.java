package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.PlanetaryResourceSet;
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


    /**
     * 将行星转换为虚空废土世界，直接写入附属专属产物列表。
     *
     * <p>不要把这些产物注册成 {@code planet_resource} 配方：本体的自然生成按
     * 哈希桶顺序取第一条 WASTELAND 配方，附属的配方可能排在本体废土配方之前，
     * 导致锻星砧搜索直接产出虚空废土星球。</p>
     */
    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        PlanetaryResourceSet resources = CelestialReformerPlanetUtil.resetWasteland(be);
        CelestialReformerPlanetUtil.addWastelandItem(resources, ResourceLocation.parse("anvilcraft:reinforced_concrete_gray"), 60);
        CelestialReformerPlanetUtil.addWastelandItem(resources, ResourceLocation.parse("anvilcraft:circuit_board"), 30);
        CelestialReformerPlanetUtil.addWastelandItem(resources, ResourceLocation.parse("anvilcraft:processor"), 5);
        CelestialReformerPlanetUtil.addWastelandItem(resources, ResourceLocation.parse("anvilcraft:void_matter"), 3);
        CelestialReformerPlanetUtil.addWastelandItem(resources, ResourceLocation.parse("anvilcraft:negative_matter_nugget"), 2);
    }
}
