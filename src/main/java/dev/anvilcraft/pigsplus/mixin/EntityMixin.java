package dev.anvilcraft.pigsplus.mixin;

import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.util.MathUtil;
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

import static dev.anvilcraft.pigsplus.util.EnderComponentConversionUtil.ConversionChance;

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
                int spiritualCount = MathUtil.getCount(ConversionChance, count, level);
                int levitationCount = count - spiritualCount;
                // 三种情况 ↓
                if (spiritualCount == count) { // 都转化成灵媒部件，如一两个机巧部件，又或者……欧洲人打过来了？
                    itemEntity.setItem(new ItemStack(AddonItems.ENDER_COMPONENT.get(), count));
                } else if (levitationCount == count) { // 都转化成漂浮粉，如一两个机巧部件，又或者……酋长我们回非洲吧（
                    itemEntity.setItem(new ItemStack(ModItems.LEVITATION_POWDER.get(), count));
                } else { // 原物品堆转化成漂浮粉，原位置生成新的灵媒核心物品堆
                    itemEntity.setItem(new ItemStack(ModItems.LEVITATION_POWDER.get(), levitationCount));
                    ItemEntity newEntity = new ItemEntity(
                        this.level,
                        itemEntity.getX(),
                        itemEntity.getY(),
                        itemEntity.getZ(),
                        new ItemStack(AddonItems.ENDER_COMPONENT.get(), spiritualCount)
                    ); // 让新物品堆和原来的动量一样
                    newEntity.setDeltaMovement(itemEntity.getDeltaMovement());
                    newEntity.setDefaultPickUpDelay();
                    this.level.addFreshEntity(newEntity);
                }
            }
        }
    }
}
