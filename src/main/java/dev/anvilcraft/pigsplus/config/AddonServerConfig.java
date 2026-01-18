package dev.anvilcraft.pigsplus.config;

import dev.anvilcraft.lib.config.BoundedDiscrete;
import dev.anvilcraft.lib.config.CollapsibleObject;
import dev.anvilcraft.lib.config.Comment;
import dev.anvilcraft.lib.config.Config;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.neoforged.fml.config.ModConfig;

@Config(name = AnvilCraftPigsPlus.MOD_ID, type = ModConfig.Type.SERVER)
public class AddonServerConfig {
    // 自动机器
    @Comment("Maximum cooldown time of auto machine (in ticks)")
    @BoundedDiscrete(min = 1, max = 80)
    public int autoMachineCooldown = 4;

    // 便携无线充电器
    @Comment("Energy conversion quantity of portable wireless charger (kW)")
    @BoundedDiscrete(min = 1, max = 1024000)
    public int portableWirelessChargerEnergyConversion = 16;

    // 电力附魔台
    @CollapsibleObject
    public ElectricEnchantingTable electricEnchantingTable = new ElectricEnchantingTable();

    public static class ElectricEnchantingTable {
        @Comment("Base power consumption limit for Electric Enchanting Table")
        @BoundedDiscrete(min = 1, max = 32768)
        public int basePowerConsumptionLimit = 8192;

        @Comment("Linear Power Consumption Coefficient Based on Experience Level")
        @BoundedDiscrete(min = 1, max = 2048)
        public double powerPerLevel = 128;

        @Comment("Quadratic Power Consumption Coefficient Based on Experience Level")
        @BoundedDiscrete(min = 0, max = 256)
        public double powerPer2Level = 16;

        @Comment("Work time for Electric Enchanting Table")
        @BoundedDiscrete(min = 1, max = 6000)
        public int workTick = 100;

        @Comment("Power consumption rate decrease per enchantPower")
        @BoundedDiscrete(min = 0, max = 1)
        public double decreasePowerRate = 0.06;
    }

    // 附魔发电机
    @CollapsibleObject
    public EnchantedGenerator enchantedGenerator = new EnchantedGenerator();

    public static class EnchantedGenerator {
        @Comment("Maximum power for Overclocking Enchanted Generator")
        @BoundedDiscrete(min = 0, max = 128000000)
        public int maxOverclockingPower = 32768;

        @Comment("Maximum power for Common Enchanted Generator")
        @BoundedDiscrete(min = 0, max = 128000000)
        public int maxCommonPower = 1024;

        @Comment("Power per enchantment's level")
        @BoundedDiscrete(min = 1, max = 1000000)
        public int powerPerLevel = 4;

        @Comment("Overclocking amplification")
        @BoundedDiscrete(min = 1, max = 100)
        public int overclockingAmplification = 4;

        @Comment("Minimum consume enchanted book cooldown")
        @BoundedDiscrete(min = 1, max = 1000)
        public int minConsumeCooldown = 5;
    }
}
