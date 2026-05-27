package dev.anvilcraft.pigsplus.integration.jade.provider;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.entity.ElectricEnchantingTableBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.CONFIG;

public enum ElectricEnchantingTableProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final Identifier UID = AnvilCraftPigsPlus.of("electric_enchanting_table");

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof ElectricEnchantingTableBlockEntity entity) {
            tag.putInt("time", entity.getTime());
            tag.putInt("maxPowerValue", CONFIG.electricEnchantingTable.basePowerConsumptionLimit);
            tag.putDouble("powerRate", entity.getPowerRate());
            tag.putInt("prevPowerValue", entity.getPrevPowerValue());
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
