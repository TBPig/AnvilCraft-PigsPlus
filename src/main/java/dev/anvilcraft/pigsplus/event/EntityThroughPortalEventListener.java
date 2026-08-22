package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.util.MathUtil;
import dev.dubhe.anvilcraft.api.event.EntityThroughPortalEvent;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static dev.anvilcraft.pigsplus.util.EnderComponentConversionUtil.ConversionChance;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class EntityThroughPortalEventListener {
    @SubscribeEvent
    public static void onEntityThroughPortal(EntityThroughPortalEvent event) {
        Level level = event.getLevel();
        if (level.isClientSide() || !(event.getEntity() instanceof ItemEntity itemEntity)) return;
        if (!itemEntity.getItem().is(AddonItems.KARAKURI_COMPONENT)) return;

        int count = itemEntity.getItem().getCount();
        int enderCount = MathUtil.getCount(ConversionChance, count, level);
        int levitationCount = count - enderCount;
        if (enderCount == count) {
            itemEntity.setItem(new ItemStack(AddonItems.ENDER_COMPONENT.get(), count));
        } else if (levitationCount == count) {
            itemEntity.setItem(new ItemStack(ModItems.LEVITATION_POWDER.get(), count));
        } else {
            itemEntity.setItem(new ItemStack(ModItems.LEVITATION_POWDER.get(), levitationCount));
            ItemEntity enderComponent = new ItemEntity(
                level,
                itemEntity.getX(),
                itemEntity.getY(),
                itemEntity.getZ(),
                new ItemStack(AddonItems.ENDER_COMPONENT.get(), enderCount)
            );
            enderComponent.setDeltaMovement(itemEntity.getDeltaMovement());
            enderComponent.setDefaultPickUpDelay();
            level.addFreshEntity(enderComponent);
        }
    }
}
