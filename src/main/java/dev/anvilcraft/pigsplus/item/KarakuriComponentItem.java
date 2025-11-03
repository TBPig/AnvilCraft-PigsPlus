package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.pigsplus.init.AddonItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;

public class KarakuriComponentItem extends Item {
    public KarakuriComponentItem(Properties properties) {
        super(properties);
    }


    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        Level level = itemEntity.level();
        ItemStack itemStack = itemEntity.getItem();
        if (!itemStack.is(AddonItems.KARAKURI_COMPONENT)) return;

        ItemEnchantments itemEnchantments = itemStack.get(DataComponents.ENCHANTMENTS);
        if (itemEnchantments == null || itemEnchantments.isEmpty()) return;

        List<Holder<Enchantment>> enchantments = itemEnchantments.keySet().stream().toList();
        int count = Math.min(5, enchantments.size());
        if (!(level.random.nextFloat() < 0.2f * count)) return;
        // 生成灵媒部件
        ItemStack resultItem = AddonItems.SPIRITUAL_COMPONENT.asStack();
        resultItem.setCount(itemStack.getCount());
        level.addFreshEntity(new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), resultItem));
    }
}