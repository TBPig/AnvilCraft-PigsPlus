package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.util.MathUtil;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public class EndPortalEventListener {
    private static final float CONVERSION_PROBABILITY = 0.2f;

    public static void onItemThrowPortal(final EntityJoinLevelEvent event) {
        // 只在服务端执行
        if (event.getLevel().isClientSide()) return;
        // 需要是物品实体
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;
        // 需要是机巧部件
        if (!itemEntity.getItem().is(AddonItems.KARAKURI_COMPONENT)) return;
        // 需要位于末地
        if (!itemEntity.level().dimension().equals(Level.END)) return;
        // 概率生成末影部件
        int originalCount = itemEntity.getItem().getCount();
        int convertedCount = MathUtil.getCount(CONVERSION_PROBABILITY, originalCount, itemEntity.level());
        int remainingCount = originalCount - convertedCount;

        itemEntity.setRemoved(Entity.RemovalReason.DISCARDED);

        if (convertedCount > 0) {
            ItemStack newItem = new ItemStack(AddonItems.ENDER_COMPONENT.asItem(), convertedCount);
            ItemEntity newItemEntity = new ItemEntity(
                itemEntity.level(),
                itemEntity.getX(),
                itemEntity.getY(),
                itemEntity.getZ(),
                newItem
            );
            itemEntity.level().addFreshEntity(newItemEntity);
        }

        if (remainingCount > 0) {
            ItemStack remainingItem = new ItemStack(ModItems.LEVITATION_POWDER.get(), remainingCount);
            ItemEntity remainingItemEntity = new ItemEntity(
                itemEntity.level(),
                itemEntity.getX(),
                itemEntity.getY(),
                itemEntity.getZ(),
                remainingItem
            );
            itemEntity.level().addFreshEntity(remainingItemEntity);
        }
    }
}
