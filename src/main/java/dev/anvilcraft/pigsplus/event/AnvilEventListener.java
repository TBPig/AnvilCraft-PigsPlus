package dev.anvilcraft.pigsplus.event;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.PigAnvilBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.api.event.AnvilEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID)
public class AnvilEventListener {
    @SubscribeEvent
    public static void onLand(AnvilEvent.OnLand event) {

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        final BlockState blockState = level.getBlockState(pos);

        if (blockState.is(AddonBlocks.PIG_ANVIL)) {
            if (event.getFallDistance() > 1) {
                if (level.random.nextDouble() < 0.01) {
                    PigAnvilBlock.damage(level, pos);
                }
            }
        }
    }
}
