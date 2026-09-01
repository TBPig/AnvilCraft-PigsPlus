package dev.anvilcraft.pigsplus.mixin;

import dev.dubhe.anvilcraft.init.item.ModItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModItemGroups.class)
public interface ModItemGroupsAccessor {
    @Invoker("isLegacyCreativeTabEnabled")
    static boolean anvilcraft$isLegacyCreativeTabEnabled() {
        throw new AssertionError();
    }
}
