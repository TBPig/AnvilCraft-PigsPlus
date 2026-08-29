package dev.anvilcraft.pigsplus.config;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.dubhe.anvilcraft.AnvilCraft;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AnvilCraftCreativeInventory {
    private AnvilCraftCreativeInventory() {
    }

    public static boolean usesLegacyLayout() {
        return AnvilCraft.CLIENT_CONFIG.useLegacyCreativeTab || isLegacyCreativeTabInConfigFile();
    }

    // Creative tabs are registered before the AnvilCraft client config field is hydrated.
    private static boolean isLegacyCreativeTabInConfigFile() {
        if (!FMLLoader.getDist().isClient()) {
            return false;
        }
        Path configFile = FMLPaths.CONFIGDIR.get().resolve(AnvilCraft.MOD_ID + "-client.toml");
        if (!Files.isRegularFile(configFile)) {
            return false;
        }
        try {
            for (String line : Files.readAllLines(configFile, StandardCharsets.UTF_8)) {
                if (line.stripLeading().startsWith("use_legacy_creative_tab") && line.contains("true")) {
                    return true;
                }
            }
        } catch (IOException e) {
            AnvilCraftPigsPlus.LOGGER.warn("Failed to read AnvilCraft client config file {}", configFile, e);
        }
        return false;
    }
}
