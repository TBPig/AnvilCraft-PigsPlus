package dev.anvilcraft.pigsplus.init;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.config.AnvilCraftCreativeInventory;
import dev.dubhe.anvilcraft.init.item.ModItemGroups;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.REGISTRATE;


public class AddonItemGroups {
    private static final DeferredRegister<CreativeModeTab> DEFERRED_REGISTER =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AnvilCraftPigsPlus.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ADDON_ITEMS = DEFERRED_REGISTER.register(
        "addon_items",
        () -> CreativeModeTab.builder()
            .icon(AddonItems.KARAKURI_COMPONENT::asStack)
            .displayItems(AddonItemTabContents::addItems)
            .title(REGISTRATE.addLang("itemGroup", AnvilCraftPigsPlus.of("addon_items"), "AnvilCraft: Pigs Plus"))
            .withTabsBefore(
                AnvilCraftCreativeInventory.usesLegacyLayout()
                    ? ModItemGroups.ANVILCRAFT_BUILDING_BLOCKS.getId()
                    : ModItemGroups.ANVILCRAFT_ITEMS.getId()
            )
            .build()
    );
    public static void register(IEventBus modEventBus) {
        DEFERRED_REGISTER.register(modEventBus);
    }
}
