package dev.anvilcraft.pigsplus.wireless;

import dev.anvilcraft.pigsplus.item.GridAdapterItem;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class GridAdapterOutputComponent extends GridAdapterComponent implements IPowerProducer {
    private int productPower;

    GridAdapterOutputComponent(ServerLevel level, BlockPos pos, Direction side, int power) {
        super(level, pos, side, power);
    }

    @Override
    int getMode() {
        return GridAdapterItem.OUTPUT_MODE;
    }

    @Override
    public PowerComponentType getComponentType() {
        return PowerComponentType.PRODUCER;
    }

    @Override
    public int getOutputPower() {
        return this.productPower;
    }

    public void setProductPower(int power) {
        if (this.productPower != power) {
            this.productPower = power;
            if (this.grid != null) {
                this.grid.markChanged();
            }
        }
    }

    @Override
    public void gridTick() {
        if (this.grid == null) return;

        IEnergyStorage storage = getEnergyStorage(this.level, this.pos, this.side);
        if (storage == null || !storage.canExtract()) {
            this.setProductPower(0);
            return;
        }

        int fePerGridTick = fePerGridTick(this.powerTarget());
        int extractEnergy = storage.extractEnergy(fePerGridTick, true);
        if (extractEnergy < fePerGridTick) {
            this.setProductPower(0);
            return;
        }

        storage.extractEnergy(fePerGridTick, false);
        this.setProductPower(this.powerTarget());
    }
}
