package dev.anvilcraft.pigsplus.integration.jei;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static dev.anvilcraft.pigsplus.item.KarakuriComponentItem.PROBABILITY;
import static dev.anvilcraft.pigsplus.util.EnderComponentConversionUtil.ConversionChance;

@JeiPlugin
public class AddonJeiPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return AnvilCraftPigsPlus.of("jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addItemStackInfo(
            AddonItems.SPIRITUAL_COMPONENT.asStack(),
            Component.translatable("jei.anvilcraft.pigsplus.info.spiritual_component", PROBABILITY*100)
        );

        registration.addItemStackInfo(
            AddonItems.ENDER_COMPONENT.asStack(),
            Component.translatable("jei.anvilcraft.pigsplus.info.ender_component", ConversionChance * 100)
        );
    }
}
