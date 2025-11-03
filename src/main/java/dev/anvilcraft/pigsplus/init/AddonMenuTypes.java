package dev.anvilcraft.pigsplus.init;

import com.tterrag.registrate.util.entry.MenuEntry;
import dev.anvilcraft.pigsplus.client.gui.screen.AutoRoyalSmithingScreen;
import dev.anvilcraft.pigsplus.client.gui.screen.ChainSmithingScreen;
import dev.anvilcraft.pigsplus.inventory.AutoRoyalSmithingMenu;
import dev.anvilcraft.pigsplus.inventory.ChainSmithingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.MOD_ID;
import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;


public class AddonMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
        DeferredRegister.create(net.minecraft.core.registries.Registries.MENU, MOD_ID);

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


    public static void register() {
    }

    public static void open(ServerPlayer player, MenuProvider provider) {
        player.openMenu(provider);
    }

    public static void open(ServerPlayer player, MenuProvider provider, BlockPos pos) {
        player.openMenu(provider, pos);
    }
}