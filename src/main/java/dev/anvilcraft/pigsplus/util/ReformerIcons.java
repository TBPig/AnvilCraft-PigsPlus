package dev.anvilcraft.pigsplus.util;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.minecraft.resources.ResourceLocation;

/**
 * 天体改造图标与槽位的统一资源定位。
 */
public final class ReformerIcons {
    public static final int ICON_SIZE = 16;
    public static final int SLOT_SIZE = 18;

    public static final ResourceLocation SLOT_CONCEPT =
        AnvilCraftPigsPlus.of("textures/gui/reformer/slot/concept_slot.png");
    public static final ResourceLocation DEFAULT =
        AnvilCraftPigsPlus.of("textures/gui/reformer/default.png");

    private ReformerIcons() {
    }

    public static ResourceLocation requirementIcon(ResourceLocation id) {
        return icon(id);
    }

    public static ResourceLocation laserIcon() {
        return AnvilCraftPigsPlus.of("textures/gui/reformer/laser.png");
    }

    private static ResourceLocation icon(ResourceLocation id) {
        String name = switch (id.getPath()) {
            case "fast_rotation", "slow_rotation", "star_fast_rotation", "star_slow_rotation", "rotation_speed" ->
                "rotation_speed";
            case "strengthen_magnetic_field", "weaken_magnetic_field",
                "star_strengthen_magnetic_field", "star_weaken_magnetic_field", "magnetic_field" ->
                "magnetic_field";
            case "increase_liquid_coverage", "decrease_liquid_coverage", "has_liquid", "no_liquid",
                "water_ocean", "liquid_coverage_range" ->
                "sea";
            case "increase_temperature", "decrease_temperature", "temperature_range" -> "temperature";
            case "add_atmosphere", "has_atmosphere", "no_atmosphere" -> "atmosphere";
            case "add_biological_resources", "has_biological_resources" -> "biologic";
            case "add_civilization", "has_civilization" -> "civilization";
            case "planetary_reformer", "star_reformer" -> "reformer";
            case "rocky_planet" -> "plante_type";
            case "laser" -> "laser";
            default -> "default";
        };
        return AnvilCraftPigsPlus.of("textures/gui/reformer/" + name + ".png");
    }
}
