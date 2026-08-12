package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
import dev.anvilcraft.pigsplus.integration.curios.CuriosCompat;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.DynamicPowerComponent;
import dev.dubhe.anvilcraft.api.power.IDynamicPowerComponentHolder;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class PortableWirelessChargerItem extends Item {
    public static final DynamicPowerComponent.PowerConsumption CONSUMPTION =
        new DynamicPowerComponent.PowerConsumption(AnvilCraftPigsPlus.CONFIG.portableWirelessChargerEnergyConversion);


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
        if (hasPortableWirelessCharger(player)) {
            powerComponent.getPowerConsumptions().add(CONSUMPTION);
        } else {
            powerComponent.getPowerConsumptions().remove(CONSUMPTION);
        }
    }

    public static void chargePlayerItems(ServerPlayer player) {
        IDynamicPowerComponentHolder holder = IDynamicPowerComponentHolder.of(player);
        DynamicPowerComponent powerComponent = holder.anvilcraft$getPowerComponent();
        PowerGrid powerGrid = powerComponent.getPowerGrid();
        if (powerGrid == null) return;
        if (!powerGrid.isWorking()) return;

        if (!hasPortableWirelessCharger(player)) return;

        int feEnergy = AnvilCraftPigsPlus.CONFIG.portableWirelessChargerEnergyConversion * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;
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

    public static boolean hasPortableWirelessCharger(ServerPlayer player) {
        if (player.getInventory().contains(AddonItems.PORTABLE_WIRELESS_CHARGER.asStack())) {
            return true;
        }
        return CuriosCompat.hasPortableWirelessCharger(player);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.portable_wireless_charger",
            AnvilCraftPigsPlus.CONFIG.portableWirelessChargerEnergyConversion,
            AnvilCraftPigsPlus.CONFIG.portableWirelessChargerEnergyConversion * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency
        ).withStyle(ChatFormatting.GRAY));
    }
}
