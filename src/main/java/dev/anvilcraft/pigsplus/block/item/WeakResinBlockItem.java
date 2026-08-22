package dev.anvilcraft.pigsplus.block.item;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.dubhe.anvilcraft.block.item.ResinBlockItem;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.init.item.ModComponents;
import dev.dubhe.anvilcraft.item.property.component.SavedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class WeakResinBlockItem extends ResinBlockItem {
    public WeakResinBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (ResinBlockItem.hasMob(stack)) return super.useOn(context);
        if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof SpawnerBlockEntity)) return super.useOn(context);

        ItemStack resinBlock = new ItemStack(ModBlocks.RESIN_BLOCK.asItem());
        InteractionResult result = super.useOn(new UseOnContext(
            context.getLevel(),
            context.getPlayer(),
            context.getHand(),
            resinBlock,
            new BlockHitResult(
                context.getClickLocation(),
                context.getClickedFace(),
                context.getClickedPos(),
                false
            )
        ));
        if (result.consumesAction() && !context.getLevel().isClientSide()) {
            stack.shrink(1);
        }
        return result;
    }

    public static InteractionResult useEntity(Player player, Entity target, ItemStack stack) {
        if (!(target instanceof Mob mob && canMobBeSaved(mob, player, stack))) {
            return InteractionResult.PASS;
        }
        saveMobInItem(player.level(), mob, player, stack);
        return InteractionResult.SUCCESS;
    }

    public static boolean canMobBeSaved(Mob entity, @Nullable Player player, @Nullable ItemStack stack) {
        if (player != null && player.getAbilities().instabuild) return true;

        if (stack != null && ResinBlockItem.hasMob(stack)) return false;

        return !(entity.getBbHeight() > 2.0 || entity.getBbWidth() > 1.5);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static ItemStack saveMobInItem(Level level, Mob entity, @Nullable Player player, ItemStack stack) {
        if (level.isClientSide()) {
            if (player == null) {
                AnvilCraftPigsPlus.LOGGER.warn("why a dispenser run saveMobInItem in client side???");
                return ItemStack.EMPTY;
            }
            Item item = stack.getItem();
            if (item instanceof WeakResinBlockItem item1) {
                BlockPos blockPos = entity.getOnPos();
                BlockState blockState = item1.getBlock().defaultBlockState();
                SoundType soundType = blockState.getSoundType(level, blockPos, player);
                level.playSound(
                    player,
                    blockPos,
                    item1.getPlaceSound(blockState, level, blockPos, player),
                    SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0f) / 2.0f,
                    soundType.getPitch() * 0.8f
                );
            }
            return ItemStack.EMPTY;
        }

        SavedEntity savedEntity = SavedEntity.fromMob(entity);

        stack.shrink(1);
        ItemStack newStack = new ItemStack(ModBlocks.RESIN_BLOCK.get());

        newStack.set(ModComponents.SAVED_ENTITY, savedEntity);
        if (entity instanceof Villager villager) {
            villager.releasePoi(MemoryModuleType.HOME);
            villager.releasePoi(MemoryModuleType.JOB_SITE);
            villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
            villager.releasePoi(MemoryModuleType.MEETING_POINT);
        }
        entity.remove(Entity.RemovalReason.DISCARDED);
        if (player != null) player.getInventory().placeItemBackInInventory(newStack);
        return newStack;
    }


}
