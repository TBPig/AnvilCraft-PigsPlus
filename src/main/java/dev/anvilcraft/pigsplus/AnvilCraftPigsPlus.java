package dev.anvilcraft.pigsplus;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import dev.anvilcraft.lib.config.ConfigManager;
import dev.anvilcraft.pigsplus.data.ModDatagen;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonItemGroups;
import dev.anvilcraft.pigsplus.init.AddonItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(AnvilCraftPigsPlus.MOD_ID)
public class AnvilCraftPigsPlus {
    public static final String MOD_ID = "anvilcraft_pigsplus";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final AddonConfig CONFIG = ConfigManager.register(AnvilCraftPigsPlus.MOD_ID, AddonConfig::new);
    public static final Registrate REGISTRATE = Registrate.create(MOD_ID);

    public AnvilCraftPigsPlus(@NotNull IEventBus modEventBus, @NotNull ModContainer modContainer) {
        AddonItemGroups.register(modEventBus);
        AddonBlocks.register();
        AddonItems.register();
        ModDatagen.init();
    }

    public static @NotNull ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
