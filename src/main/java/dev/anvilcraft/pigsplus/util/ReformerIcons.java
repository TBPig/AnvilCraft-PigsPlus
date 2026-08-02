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

    public static ResourceLocation laserIcon() {
        return AnvilCraftPigsPlus.of("textures/gui/reformer/laser.png");
    }
}
