package dev.anvilcraft.pigsplus.integration.jade;

import dev.anvilcraft.pigsplus.block.ElectricEnchantingTableBlock;
import dev.anvilcraft.pigsplus.block.PecisionMagneticPivotBlock;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import dev.anvilcraft.pigsplus.block.entity.PecisionMagneticPivotBlockEntity;
import dev.anvilcraft.pigsplus.integration.jade.provider.CelestialReformerJadeProvider;
import dev.anvilcraft.pigsplus.integration.jade.provider.ElectricEnchantingTableProvider;
import dev.anvilcraft.pigsplus.integration.jade.provider.PecisionMagneticPivotProvider;
import dev.dubhe.anvilcraft.block.cfa.CelestialForgingAnvilBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@SuppressWarnings("unused")
@WailaPlugin
public class AddonJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
//        registration.registerItemStorage(CrabTrapStorageProvider.INSTANCE, CrabTrapBlockEntity.class);
        registration.registerBlockDataProvider(ElectricEnchantingTableProvider.INSTANCE, ElectricEnchantingTableBlockEntity.class);
        registration.registerBlockDataProvider(PecisionMagneticPivotProvider.INSTANCE, PecisionMagneticPivotBlockEntity.class);
        registration.registerBlockDataProvider(CelestialReformerJadeProvider.INSTANCE, CelestialForgingAnvilBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
//        registration.registerItemStorageClient(CrabTrapStorageProvider.INSTANCE);
        registration.registerBlockComponent(ElectricEnchantingTableProvider.INSTANCE, ElectricEnchantingTableBlock.class);
        registration.registerBlockComponent(PecisionMagneticPivotProvider.INSTANCE, PecisionMagneticPivotBlock.class);
        registration.registerBlockComponent(CelestialReformerJadeProvider.INSTANCE, CelestialForgingAnvilBlock.class);
    }
}
