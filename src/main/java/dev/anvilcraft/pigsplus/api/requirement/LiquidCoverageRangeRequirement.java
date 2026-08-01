package dev.anvilcraft.pigsplus.api.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dubhe.anvilcraft.block.entity.CelestialForgingAnvilBlockEntity;
import dev.dubhe.anvilcraft.block.entity.celestial.CelestialBodyData;
import dev.dubhe.anvilcraft.block.entity.celestial.LiquidCoverage;
import dev.dubhe.anvilcraft.block.entity.celestial.RockyPlanetData;
import dev.dubhe.anvilcraft.block.entity.celestial.SpecialCelestialBodyData;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 液体覆盖度区间需求，边界为可选的 {@link LiquidCoverage}。
 */
public class LiquidCoverageRangeRequirement extends ReformerRequirement {
    public static final MapCodec<LiquidCoverageRangeRequirement> CODEC =
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            coverageCodec().optionalFieldOf("min").forGetter(req -> Optional.ofNullable(req.min)),
            coverageCodec().optionalFieldOf("max").forGetter(req -> Optional.ofNullable(req.max))
        ).apply(instance, (min, max) -> new LiquidCoverageRangeRequirement(min.orElse(null), max.orElse(null))));

    private final @Nullable LiquidCoverage min;
    private final @Nullable LiquidCoverage max;

    public LiquidCoverageRangeRequirement() {
        this(null, null);
    }

    public LiquidCoverageRangeRequirement(@Nullable LiquidCoverage min, @Nullable LiquidCoverage max) {
        this.min = min;
        this.max = max;
    }

    private static Codec<LiquidCoverage> coverageCodec() {
        return Codec.STRING.xmap(LiquidCoverage::fromName, LiquidCoverage::getSerializedName);
    }

    @Override
    public MapCodec<? extends ReformerRequirement> codec() {
        return CODEC;
    }

    @Override
    public Component getDescription() {
        if (this.min != null && this.max != null) {
            return this.text(
                "requirement.anvilcraft_pigsplus.liquid_coverage_between",
                this.min.getSerializedName(),
                this.max.getSerializedName()
            );
        }
        if (this.min != null) {
            return this.text("requirement.anvilcraft_pigsplus.liquid_coverage_at_least", this.min.getSerializedName());
        }
        if (this.max != null) {
            return this.text("requirement.anvilcraft_pigsplus.liquid_coverage_at_most", this.max.getSerializedName());
        }
        return this.text("requirement.anvilcraft_pigsplus.unknown");
    }

    @Override
    public boolean test(CelestialForgingAnvilBlockEntity be) {
        CelestialBodyData body = be.getCelestialBodyData();
        LiquidCoverage coverage;
        if (body instanceof RockyPlanetData rp) {
            coverage = rp.liquidCoverage();
        } else if (body instanceof SpecialCelestialBodyData sp) {
            coverage = sp.liquidCoverage() == null ? LiquidCoverage.NONE : sp.liquidCoverage();
        } else {
            return false;
        }
        int value = coverage.ordinal();
        int min = this.min == null ? 0 : this.min.ordinal();
        int max = this.max == null ? LiquidCoverage.values().length - 1 : this.max.ordinal();
        return value >= min && value <= max;
    }
}
