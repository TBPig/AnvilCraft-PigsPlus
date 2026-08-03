package dev.anvilcraft.pigsplus.wireless;

import dev.anvilcraft.pigsplus.item.GridAdapterItem;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerComponentType;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class GridAdapterInputComponent extends GridAdapterComponent implements IPowerConsumer {
    private int concumePower;

    GridAdapterInputComponent(ServerLevel level, BlockPos pos, Direction side, int power) {
        super(level, pos, side, power);
    }

    @Override
    int getMode() {
        return GridAdapterItem.INPUT_MODE;
    }

    @Override
    public PowerComponentType getComponentType() {
        return PowerComponentType.CONSUMER;
    }

    private void setConcumePower(int power) {
        if (this.concumePower != power) {
            this.concumePower = power;
            if (this.grid != null) {
                this.grid.markChanged();
            }
        }
    }

    @Override
    public int getInputPower() {
        return this.concumePower;
    }

    @Override
    public void gridTick() {
        if (this.grid == null || !this.grid.isWorking()) return;

        IEnergyStorage storage = getEnergyStorage(this.level, this.pos, this.side);
        if (storage == null || !storage.canReceive()) {
            this.setConcumePower(0);
            return;
        }

        int fePerGridTick = fePerGridTick(this.powerTarget());
        int accepted = storage.receiveEnergy(fePerGridTick, false);

        if (accepted <= 0) {
            this.setConcumePower(0);
            return;
        }

        int consumePower = Math.ceilDiv(
            Math.ceilDiv(accepted, PowerGrid.GRID_TICK),
            AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency
        );
        this.setConcumePower(consumePower);
    }
}
