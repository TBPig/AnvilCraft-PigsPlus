package dev.anvilcraft.pigsplus.api.modification;

import dev.anvilcraft.pigsplus.util.CelestialReformerPlanetUtil;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyClass;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 提升一级指定液体的海洋覆盖度，并重新生成行星资源。
 */
public class IncreaseOceanCoverageModification extends ReformerModification {
    private final ResourceLocation oceanFluid;
    private final String descriptionKey;

    public IncreaseOceanCoverageModification(ResourceLocation oceanFluid, String descriptionKey) {
        this.oceanFluid = oceanFluid;
        this.descriptionKey = descriptionKey;
    }

    @Override
    public Component getDescription() {
        return this.text(this.descriptionKey);
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
            case NONE -> LiquidCoverage.LOW;
            case LOW -> LiquidCoverage.MEDIUM;
            case MEDIUM -> LiquidCoverage.HIGH;
            case HIGH -> null;
        };
        if (next == null) return;

        CelestialBodyClass nextClass = switch (next) {
            case LOW -> CelestialBodyClass.ROCKY_LOW_LIQUID;
            case MEDIUM -> CelestialBodyClass.ROCKY_MED_LIQUID;
            case HIGH -> CelestialBodyClass.ROCKY_HIGH_LIQUID;
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
        CelestialReformerPlanetUtil.regenerate(be, updated, this.oceanFluid);
    }
}
