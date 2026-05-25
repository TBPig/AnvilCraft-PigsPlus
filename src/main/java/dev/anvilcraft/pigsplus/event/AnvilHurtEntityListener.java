package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.pig.Pig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class AnvilHurtEntityListener {
    public static final String NAME = "triple_bpig";
    public static final double MAGIC_NUM = 1.6 * 0.1;

    @SubscribeEvent
    public static void onAnvilHurtEntity(AnvilEvent.HurtEntity event) {
        Entity entity = event.getHurtedEntity();
        ServerLevel level = event.getLevel();
        if (level.isClientSide()) return;

        if (entity instanceof Pig pig) {
            if (pig.getName().getString().equals(NAME)) {
                entity.spawnAtLocation(level, AddonBlocks.PIG_ANVIL.asStack(3));
            } else if (level.getRandom().nextDouble() < MAGIC_NUM) {
                entity.spawnAtLocation(level, AddonBlocks.PIG_ANVIL);
            }
        }
    }
}
