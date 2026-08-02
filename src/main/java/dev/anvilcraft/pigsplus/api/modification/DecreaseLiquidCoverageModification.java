package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.init.AddonDataAttachments;
import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.anvilcraft.pigsplus.util.OceanEnchantmentData;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyClass;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 降低一级流体覆盖率，并重新生成行星资源。
 */
public class DecreaseLiquidCoverageModification extends ReformerModification {
    @Override
    public Component getDescription() {
        return this.text("modification.anvilcraft_pigsplus.decrease_liquid_coverage");
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon("sea");
    }

    @Override
    public void apply(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        if (!(body instanceof RockyPlanetData rp)) return;

        LiquidCoverage next = switch (rp.liquidCoverage()) {
            case HIGH -> LiquidCoverage.MEDIUM;
            case MEDIUM -> LiquidCoverage.LOW;
            case LOW -> LiquidCoverage.NONE;
            case NONE -> null;
        };
        if (next == null) return;

        CelestialBodyClass nextClass = switch (next) {
            case NONE -> CelestialBodyClass.ROCKY_NO_LIQUID;
            case LOW -> CelestialBodyClass.ROCKY_LOW_LIQUID;
            case MEDIUM -> CelestialBodyClass.ROCKY_MED_LIQUID;
            default -> rp.bodyClass();
        };
        RockyPlanetData updated = new RockyPlanetData(
            nextClass,
            rp.hasAtmosphere(),
            next,
            rp.temperature(),
            rp.ringType(),
            rp.size(),
            rp.paletteBaseRow(),
            rp.paletteOverlayRow(),
            rp.axialTilt(),
            rp.rotationSpeed(),
            rp.magneticFieldStrength()
        );
        if (next == LiquidCoverage.NONE) {
            be.setData(AddonDataAttachments.OCEAN_ENCHANTMENT, OceanEnchantmentData.EMPTY);
        }
        CelestialReformerPlanetUtil.regenerate(be, updated);
    }
}
