package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonItems;
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
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.function.Consumer;

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
        boolean isInInventory = player.getInventory().contains(AddonItems.PORTABLE_WIRELESS_CHARGER.asStack());
        if (isInInventory) {
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

        boolean isInInventory = player.getInventory().contains(AddonItems.PORTABLE_WIRELESS_CHARGER.asStack());
        if (!isInInventory) return;

        int feEnergy = AnvilCraftPigsPlus.CONFIG.portableWirelessChargerEnergyConversion * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;
        // 遍历玩家物品栏，尝试为有能量槽的物品充电
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack.isEmpty()) continue;

            EnergyHandler itemEnergy = itemStack.getCapability(Capabilities.Energy.ITEM, null);
            if (itemEnergy == null) continue;

            try (Transaction transaction = Transaction.openRoot()) {
                int receiveEnergy = itemEnergy.insert(feEnergy, transaction);
                feEnergy -= receiveEnergy;
                transaction.commit();
            }

            if (feEnergy <= 0) break;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
        ItemStack itemStack,
        TooltipContext context,
        TooltipDisplay display,
        Consumer<Component> builder,
        TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(Component.translatable(
            "tooltip.anvilcraft_pigsplus.portable_wireless_charger",
            AnvilCraftPigsPlus.CONFIG.portableWirelessChargerEnergyConversion,
            AnvilCraftPigsPlus.CONFIG.portableWirelessChargerEnergyConversion * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency
        ).withStyle(ChatFormatting.GRAY));
    }
}