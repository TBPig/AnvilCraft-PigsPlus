package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.DynamicPowerComponent;
import dev.dubhe.anvilcraft.api.power.IDynamicPowerComponentHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class PortableWirelessChargerItem extends Item {
    public static final DynamicPowerComponent.PowerConsumption CONSUMPTION = new DynamicPowerComponent.PowerConsumption(512);


    public PortableWirelessChargerItem(Properties properties) {
        super(properties);
    }

    public static void playerTick(ServerPlayer player) {
        refreshPower(player);
        chargePlayerItems(player);
    }

    public static void refreshPower(ServerPlayer player) {
        IDynamicPowerComponentHolder holder = IDynamicPowerComponentHolder.of(player);
        DynamicPowerComponent powerComponent = holder.anvilcraft$getPowerComponent();
        boolean isInInventory = player.getInventory().contains(AddonItems.PORTABLE_WIRELESS_CHARGER.asStack());
        if (isInInventory) {
            powerComponent.getPowerConsumptions().add(CONSUMPTION);
        } else {
            powerComponent.getPowerConsumptions().remove(CONSUMPTION);
        }
    }

    public static void chargePlayerItems(ServerPlayer player) {
        boolean isInInventory = player.getInventory().contains(AddonItems.PORTABLE_WIRELESS_CHARGER.asStack());
        int feEnergy = CONSUMPTION.amount() * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;
        if (!isInInventory) return;
        // 遍历玩家物品栏，尝试为有能量槽的物品充电
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack.isEmpty()) continue;

            IEnergyStorage itemEnergy = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy == null) continue;
            if (!itemEnergy.canReceive()) continue;

            int receiveEnergy = itemEnergy.receiveEnergy(feEnergy, false);
            feEnergy -= receiveEnergy;
            if (feEnergy <= 0) break;
        }
    }


}