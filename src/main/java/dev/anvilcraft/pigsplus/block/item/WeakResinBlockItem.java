package dev.anvilcraft.pigsplus.block.item;

import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.item.block.ResinBlockItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class WeakResinBlockItem extends ResinBlockItem {
    public WeakResinBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static InteractionResult useEntity(Player player, Entity target, ItemStack stack) {
        if (!(target instanceof Mob mob && canMobBeSaved(mob, player, stack))) {
            return InteractionResult.PASS;
        }
        if (!player.level().isClientSide()) {
            stack.shrink(1);
        }
        ItemStack newStack = new ItemStack(ModBlocks.RESIN_BLOCK.get());
        ResinBlockItem.saveMobInItem(player.level(), mob, player, newStack);
        return InteractionResult.SUCCESS;
    }

    public static boolean canMobBeSaved(Mob entity, @Nullable Player player, @Nullable ItemStack stack) {
        if (player != null && player.getAbilities().instabuild) return true;

        if (stack != null && ResinBlockItem.hasMob(stack)) return false;

        return !(entity.getBbHeight() > 2.0 || entity.getBbWidth() > 1.5);
    }
}