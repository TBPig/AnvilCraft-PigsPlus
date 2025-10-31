package dev.anvilcraft.pigsplus.client;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod(value = AnvilCraftPigsPlus.MOD_ID, dist = Dist.CLIENT)
public class AnvilCraftPigsPlusClient {
    public AnvilCraftPigsPlusClient(@NotNull IEventBus modBus, @NotNull ModContainer container) {
    }
}
