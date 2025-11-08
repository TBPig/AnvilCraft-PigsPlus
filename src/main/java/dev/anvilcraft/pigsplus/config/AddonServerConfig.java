package dev.anvilcraft.pigsplus.config;

import dev.anvilcraft.lib.config.BoundedDiscrete;
import dev.anvilcraft.lib.config.CollapsibleObject;
import dev.anvilcraft.lib.config.Comment;
import dev.anvilcraft.lib.config.Config;
import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import net.neoforged.fml.config.ModConfig;

@Config(name = AnvilCraftPigsPlus.MOD_ID, type = ModConfig.Type.SERVER)
public class AddonServerConfig {
    // TODO:修复config不完全问题
    // 自动皇家锻造台
    @Comment("Maximum cooldown time of auto royal smithing table (in ticks)")
    @BoundedDiscrete(min = 1, max = 80)
    public int autoRoyalSmithingTableCooldown = 4;

    // 附魔发电机
    @CollapsibleObject
    public EnchantedGenerator enchantedGenerator = new EnchantedGenerator();

    // 电力附魔台
    @CollapsibleObject
    public ElectricEnchantingTable electricEnchantingTable = new ElectricEnchantingTable();


    public static class ElectricEnchantingTable {
        @Comment("Base power consumption limit for Electric Enchanting Table")
        @BoundedDiscrete(min = 1, max = 32768)
        public int basePowerConsumptionLimit = 1024;

        @Comment("Linear Power Consumption Coefficient Based on Experience Level")
        @BoundedDiscrete(min = 1, max = 2048)
        public double powerPerLevel = 128;

        @Comment("Quadratic Power Consumption Coefficient Based on Experience Level")
        @BoundedDiscrete(min = 0, max = 256)
        public double powerPer2Level = 16;

        @Comment("Work time for Electric Enchanting Table")
        @BoundedDiscrete(min = 1, max = 6000)
        public int workTick = 200;

        @Comment("Power consumption rate decrease per bookShelf")
        @BoundedDiscrete(min = 0, max = 1)
        public double decreasePowerRate = 0.02;

        @Comment("Power consumption limit increase per core")
        @BoundedDiscrete(min = 1, max = 16384)
        public int powerAddition = 256;
    }

    public static class EnchantedGenerator {
        @Comment("Maximum power for Overclocking Enchanted Generator")
        @BoundedDiscrete(min = 0)
        public int maxOverclockingPower = 32768;

        @Comment("Maximum power for Common Enchanted Generator")
        @BoundedDiscrete(min = 0)
        public int maxCommonPower = 1024;

        @Comment("Power per enchantment's level")
        @BoundedDiscrete(min = 1)
        public int powerPerLevel = 2;

        @Comment("Overclocking amplification")
        @BoundedDiscrete(min = 1, max = 10)
        public int overclockingAmplification = 2;

        @Comment("Minimum consume enchanted book cooldown")
        @BoundedDiscrete(min = 1, max = 1000)
        public int minConsumeCooldown = 5;
    }
}
