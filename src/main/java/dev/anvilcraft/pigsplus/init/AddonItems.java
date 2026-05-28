package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.lib.v2.registrum.util.entry.ItemEntry;
import dev.anvilcraft.pigsplus.data.recipe.RegistrumItemRecipeLoader;
import dev.anvilcraft.pigsplus.item.KarakuriComponentItem;
import dev.anvilcraft.pigsplus.item.MengerSpongeStaffItem;
import dev.anvilcraft.pigsplus.item.PortableWirelessChargerItem;
import dev.dubhe.anvilcraft.util.registrater.DataGenUtil;
import net.minecraft.world.item.Item;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;

@SuppressWarnings("unused")
public class AddonItems {
    static {
        REGISTRATE.defaultCreativeTab(AddonItemGroups.ADDON_ITEMS.getKey());
    }

    public static final ItemEntry<KarakuriComponentItem> KARAKURI_COMPONENT = REGISTRATE
        .item("karakuri_component", KarakuriComponentItem::new)
        .recipe(RegistrumItemRecipeLoader::karakuriComponent)
        .register();

    public static final ItemEntry<Item> SPIRITUAL_COMPONENT = REGISTRATE
        .item("spiritual_component", Item::new)
        .register();

    public static final ItemEntry<Item> ENDER_COMPONENT = REGISTRATE
        .item("ender_component", Item::new)
        .register();

    public static final ItemEntry<Item> ENDER_SEED = REGISTRATE
        .item("ender_seed", Item::new)
        .recipe(RegistrumItemRecipeLoader::enderSeed)
        .register();

    public static final ItemEntry<Item> ECHO_GEODE = REGISTRATE
        .item("echo_geode", Item::new)
        .register();

    public static final ItemEntry<Item> CHAOTIC_RAW_ORE = REGISTRATE
        .item("chaotic_raw_ore", Item::new)
        .recipe(RegistrumItemRecipeLoader::chaoticRawOre)
        .register();

    public static final ItemEntry<PortableWirelessChargerItem> PORTABLE_WIRELESS_CHARGER = REGISTRATE
        .item("portable_wireless_charger", PortableWirelessChargerItem::new)
        .properties((properties) -> properties.stacksTo(1))
        .recipe(RegistrumItemRecipeLoader::portableWirelessCharger)
        .register();

    public static final ItemEntry<MengerSpongeStaffItem> MENGER_SPONGE_STAFF = REGISTRATE
        .item("menger_sponge_staff", MengerSpongeStaffItem::new)
        .properties((properties) -> properties.stacksTo(1))
        .model(DataGenUtil::onlyInfo)
        .recipe(RegistrumItemRecipeLoader::mengerSpongeStaff)
        .register();

    public static void register() {
    }
}