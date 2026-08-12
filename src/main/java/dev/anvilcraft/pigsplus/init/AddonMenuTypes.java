package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.MenuEntry;
import dev.anvilcraft.pigsplus.client.gui.screen.AutoJewelCraftingScreen;
import dev.anvilcraft.pigsplus.client.gui.screen.AutoRoyalGrindstoneScreen;
import dev.anvilcraft.pigsplus.client.gui.screen.AutoRoyalSmithingScreen;
import dev.anvilcraft.pigsplus.client.gui.screen.ChainSmithingScreen;
import dev.anvilcraft.pigsplus.client.gui.screen.ExperienceInterfaceScreen;
import dev.anvilcraft.pigsplus.client.gui.screen.GridAdapterScreen;
import dev.anvilcraft.pigsplus.client.gui.screen.PortableWirelessChargerScreen;
import dev.anvilcraft.pigsplus.inventory.AutoJewelCraftingMenu;
import dev.anvilcraft.pigsplus.inventory.AutoRoyalGrindstoneMenu;
import dev.anvilcraft.pigsplus.inventory.AutoRoyalSmithingMenu;
import dev.anvilcraft.pigsplus.inventory.ChainSmithingMenu;
import dev.anvilcraft.pigsplus.inventory.ExperienceInterfaceMenu;
import dev.anvilcraft.pigsplus.inventory.GridAdapterMenu;
import dev.anvilcraft.pigsplus.inventory.PortableWirelessChargerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.MOD_ID;
import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;


public class AddonMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
        DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final MenuEntry<ChainSmithingMenu> CHAIN_SMITHING = REGISTRATE
        .menu(
            "chain_smithing",
            (type, id, inv) -> new ChainSmithingMenu(type, id, inv),
            () -> ChainSmithingScreen::new
        )
        .register();

    public static final MenuEntry<AutoRoyalSmithingMenu> AUTO_ROYAL_SMITHING = REGISTRATE
        .menu("auto_royal_smithing", AutoRoyalSmithingMenu::new, () -> AutoRoyalSmithingScreen::new)
        .register();

    public static final MenuEntry<AutoRoyalGrindstoneMenu> AUTO_ROYAL_GRINDSTONE = REGISTRATE
        .menu("auto_royal_grindstone", AutoRoyalGrindstoneMenu::new, () -> AutoRoyalGrindstoneScreen::new)
        .register();

    public static final MenuEntry<AutoJewelCraftingMenu> AUTO_JEWEL_CRAFTING = REGISTRATE
        .menu("auto_jewel_crafting", AutoJewelCraftingMenu::new, () -> AutoJewelCraftingScreen::new)
        .register();

    public static final MenuEntry<ExperienceInterfaceMenu> EXPERIENCE_INTERFACE = REGISTRATE
        .menu(
            "experience_interface",
            (type, id, inv) -> new ExperienceInterfaceMenu(type, id),
            () -> ExperienceInterfaceScreen::new
        )
        .register();

    public static final MenuEntry<GridAdapterMenu> GRID_ADAPTER = REGISTRATE
        .menu(
            "grid_adapter",
            (type, id, inv) -> new GridAdapterMenu(type, id),
            () -> GridAdapterScreen::new
        )
        .register();

    public static final MenuEntry<PortableWirelessChargerMenu> PORTABLE_WIRELESS_CHARGER = REGISTRATE
        .menu(
            "portable_wireless_charger",
            (type, id, inv) -> new PortableWirelessChargerMenu(type, id),
            () -> PortableWirelessChargerScreen::new
        )
        .register();

    public static void register() {
    }

    public static void open(ServerPlayer player, MenuProvider provider) {
        player.openMenu(provider);
    }

    public static void open(ServerPlayer player, MenuProvider provider, BlockPos pos) {
        player.openMenu(provider, pos);
    }
}
