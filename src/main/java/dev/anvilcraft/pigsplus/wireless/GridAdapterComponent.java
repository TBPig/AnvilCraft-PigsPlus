package dev.anvilcraft.pigsplus.wireless;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.IPowerComponent;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

abstract class GridAdapterComponent implements IPowerComponent {
    protected final ServerLevel level;
    protected final BlockPos pos;
    protected final Direction side;
    protected int power;
    @Nullable
    protected PowerGrid grid;

    GridAdapterComponent(ServerLevel level, BlockPos pos, Direction side, int power) {
        this.level = level;
        this.pos = pos.immutable();
        this.side = side;
        this.power = power;
    }

    static @Nullable IEnergyStorage getEnergyStorage(Level level, BlockPos pos, Direction side) {
        IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
        return storage != null ? storage : level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
    }

    abstract int getMode();

    Direction getSide() {
        return this.side;
    }

    int getPower() {
        return this.power;
    }

    void setPower(int power) {
        if (this.power == power) return;
        this.power = power;
        if (this.grid != null) {
            this.grid.markChanged();
        }
    }

    @Override
    public Level getCurrentLevel() {
        return this.level;
    }

    @Override
    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return this.grid;
    }

    protected int powerTarget() {
        return this.power;
    }

    protected static int fePerGridTick(int power) {
        long amount = (long) power
                      * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency
                      * PowerGrid.GRID_TICK;
        return (int) Math.min(amount, Integer.MAX_VALUE);
    }
}
