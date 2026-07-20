package dev.anvilcraft.pigsplus.init;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AddonInteractionMaps {
    public static final CauldronInteraction.InteractionMap VOID_ACID = CauldronInteraction.newInteractionMap("void_acid");

    public static void init() {
        var map = VOID_ACID.map();
        map.put(
            Items.BUCKET,
            (state, level, pos, player, hand, stack) -> CauldronInteraction.fillBucket(
                state,
                level,
                pos,
                player,
                hand,
                stack,
                new ItemStack(AddonItems.VOID_ACID_BUCKET.get()),
                s -> true,
                SoundEvents.BUCKET_FILL
            )
        );

        var emptyMap = CauldronInteraction.EMPTY.map();
        emptyMap.put(
            AddonItems.VOID_ACID_BUCKET.get(),
            (state, level, pos, player, hand, stack) -> CauldronInteraction.emptyBucket(
                level,
                pos,
                player,
                hand,
                stack,
                AddonBlocks.VOID_ACID_CAULDRON.get().fullFilled(),
                SoundEvents.BUCKET_EMPTY
            )
        );
    }
}
