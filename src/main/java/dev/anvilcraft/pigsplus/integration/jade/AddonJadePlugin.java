package dev.anvilcraft.pigsplus.integration.jade;

import dev.anvilcraft.pigsplus.integration.jade.provider.ElectricEnchantingTableProvider;
import dev.anvilcraft.pigsplus.integration.jade.provider.client.ElectricEnchantingTableClientProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@SuppressWarnings("unused")
@WailaPlugin
public class AddonJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ElectricEnchantingTableProvider.INSTANCE, BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ElectricEnchantingTableClientProvider.INSTANCE, Block.class);
    }
}
