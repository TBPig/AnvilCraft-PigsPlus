package dev.anvilcraft.pigsplus.config;

import dev.anvilcraft.lib.v2.config.BoundedDiscrete;
import dev.anvilcraft.lib.v2.config.CollapsibleObject;
import dev.anvilcraft.lib.v2.config.Comment;
import dev.anvilcraft.lib.v2.config.Config;
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

    // 虚空酸
    @Comment("Allow the Void Acid to destroy blocks")
    public boolean voidAcidDestroy = true;

    // 行星/恒星改造器
    @Comment("Absorption interval of planetary/star reformer (in ticks)")
    @BoundedDiscrete(min = 1, max = 1000)
    public int reformerAbsorptionCooldown = 8;

    // 电力附魔台
    @CollapsibleObject
    public ElectricEnchantingTable electricEnchantingTable = new ElectricEnchantingTable();

    public static class ElectricEnchantingTable {
        @Comment("XP saving rate")
        @BoundedDiscrete(min = 0, max = 1)
        public double decreaseRate = 0.05;

        @Comment("The speed of absorbing liquid experience")
        @BoundedDiscrete(min = 1, max = 1000000)
        public int fluidComsumeSpeed = 100;

        @BoundedDiscrete(min = 1, max = 1024000)
        public int power = 256;
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
        public int powerPerLevel = 2;

        @Comment("Overclocking amplification")
        @BoundedDiscrete(min = 1, max = 100)
        public int overclockingAmplification = 8;

        @Comment("Minimum consume enchanted book cooldown")
        @BoundedDiscrete(min = 1, max = 1000)
        public int minConsumeCooldown = 10;
    }
}
