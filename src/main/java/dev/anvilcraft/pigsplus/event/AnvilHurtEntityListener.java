package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class AnvilHurtEntityListener {
    @SubscribeEvent
    public static void onAnvilHurtEntity(@NotNull AnvilEvent.HurtEntity event) {
        Entity entity = event.getHurtedEntity();
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        if (entity instanceof Pig pig) {
            if (pig.getName().getString().equals("triple_bpig")) {
                entity.spawnAtLocation(AddonBlocks.PIG_ANVIL.asStack(64));
            } else if (level.random.nextDouble() < 0.5) {
                entity.spawnAtLocation(AddonBlocks.PIG_ANVIL);
            }
        }
    }
}
