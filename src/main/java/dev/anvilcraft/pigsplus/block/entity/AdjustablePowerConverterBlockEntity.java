package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerComponentType;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.inventory.SliderMenu;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class AdjustablePowerConverterBlockEntity extends BlockEntity
    implements IPowerConsumer, IPowerProducer, MenuProvider {
    private @Nullable PowerGrid grid;
    private int powerTarget = 16; // kW能量
    private int time = 0;

    @Getter
    private boolean working = false;

    public final EnergyStorage feEnergy = new EnergyStorage(128000000);

    public AdjustablePowerConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("feEnergy", this.feEnergy.serializeNBT(provider));
        tag.putInt("powerTarget", this.powerTarget);
        tag.putBoolean("working", this.working);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.feEnergy.deserializeNBT(provider, Objects.requireNonNull(tag.get("feEnergy")));
        this.powerTarget = tag.getInt("powerTarget");
        this.working = tag.getBoolean("working");
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public @Nullable PowerGrid getGrid() {
        return this.grid;
    }

    @Override
    public int getOutputPower() {
        return this.working ? Math.max(this.powerTarget, 0) : 0;
    }

    @Override
    public int getInputPower() {
        return this.powerTarget < 0 ? -this.powerTarget : 0;
    }

    @Override
    public PowerComponentType getComponentType() {
        return this.powerTarget >= 0 ? PowerComponentType.PRODUCER : PowerComponentType.CONSUMER;
    }

    @Override
    public int getRange() {
        return 2;
    }

    @Override
    public void gridTick() {
        if (this.powerTarget < 0) {
            this.working = this.grid != null && this.grid.isWorking();
        }
    }

    public void clientTick() {
        time += 1;
    }

    public void tick(Level level, BlockPos pos) {
        this.flushState(level, pos);
        if (level.isClientSide()) return;

        setChanged();
        if (powerTarget >= 0) {
            extractFE();
        } else {
            receiveFE();
            outputFE();
        }
    }

    private void outputFE() {
        if (level == null) return;
        // 向每个方向输出能量
        for (Direction direction : Direction.values()) {
            if (feEnergy.getEnergyStored() <= 0) break;

            BlockPos adjacentPos = getBlockPos().relative(direction);
            BlockEntity adjacentBlockEntity = level.getBlockEntity(adjacentPos);
            if (adjacentBlockEntity == null) continue;

            IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, adjacentPos, direction.getOpposite());
            if (energyStorage == null) continue;

            int receiveEnergy = energyStorage.receiveEnergy(feEnergy.getEnergyStored(), false);
            feEnergy.extractEnergy(receiveEnergy, false);

        }

    }

    private void extractFE() {
        int feConverted = powerTarget * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;
        if (this.feEnergy.extractEnergy(feConverted, true) < feConverted) {
            this.working = false;
            return;
        }
        this.feEnergy.extractEnergy(feConverted, false);
        this.working = true;
    }

    private void receiveFE() {
        if (grid == null || !grid.isWorking()) return;
        if (!working) return;

        // 如果存储满了，停止消耗电网能量
        int feConverted = -powerTarget * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;
        feEnergy.receiveEnergy(feConverted, false);
    }

    @Override
    public Component getDisplayName() {
        return AddonBlocks.ADJUSTABLE_POWER_CONVERTER.get().getName();
    }

    public void setTarget(int powerTarget) {
        this.powerTarget = powerTarget;
        this.working = false;
        if (this.grid != null) {
            this.grid.markChanged();
        }
        setChanged();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (player.isSpectator()) return null;
        return new SliderMenu(i, this::setTarget);
    }

    public EnergyStorage getEnergyStorage() {
        return this.feEnergy;
    }
}
