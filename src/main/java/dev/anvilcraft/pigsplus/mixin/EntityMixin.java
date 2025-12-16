package dev.anvilcraft.pigsplus.mixin;

import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.api.injection.entity.IEntityExtension;
import dev.dubhe.anvilcraft.init.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityExtension {

    @Shadow
    private Level level;

    @Inject(
        method = "handlePortal", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/world/entity/Entity;changeDimension(" + "Lnet/minecraft/world/level/portal/DimensionTransition;" + ")" + "Lnet/minecraft/world/entity/Entity;"
    )
    )
    private void handlePortal(CallbackInfo ci) {
        if ((Object) this instanceof ItemEntity itemEntity) {
            if (itemEntity.getItem().is(AddonItems.KARAKURI_COMPONENT)) {
                int count = itemEntity.getItem().getCount();
                int spiritualCount = 0;
                int levitationCount = 0;
                // 循环使用随机数判断漂浮粉和灵媒部件的数量
                for (int i = 0; i < count; i++) {
                    if (this.level.random.nextDouble() < 0.2) spiritualCount++;
                    else levitationCount++;
                } // 三种情况 ↓
                if (spiritualCount == count) { // 都转化成灵媒部件，如一两个机巧部件，又或者……欧洲人打过来了？
                    itemEntity.setItem(new ItemStack(AddonItems.SPIRITUAL_COMPONENT.get(), count));
                } else if (levitationCount == count) { // 都转化成漂浮粉，如一两个机巧部件，又或者……酋长我们回非洲吧（
                    itemEntity.setItem(new ItemStack(ModItems.LEVITATION_POWDER.get(), count));
                } else { // 原物品堆转化成漂浮粉，原位置生成新的灵媒核心物品堆
                    itemEntity.setItem(new ItemStack(ModItems.LEVITATION_POWDER.get(), levitationCount));
                    ItemEntity newEntity = new ItemEntity(
                        this.level,
                        itemEntity.getX(),
                        itemEntity.getY(),
                        itemEntity.getZ(),
                        new ItemStack(AddonItems.SPIRITUAL_COMPONENT.get(), spiritualCount)
                    ); // 让新物品堆和原来的动量一样
                    newEntity.setDeltaMovement(itemEntity.getDeltaMovement());
                    this.level.addFreshEntity(newEntity);
                }
            }
        }
    }
}
