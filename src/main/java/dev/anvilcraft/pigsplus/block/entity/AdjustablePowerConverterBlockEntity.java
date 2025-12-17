package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerComponentType;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.inventory.SliderMenu;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
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
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

@Getter
public class AdjustablePowerConverterBlockEntity extends BlockEntity
    implements IPowerConsumer, IPowerProducer, MenuProvider {
    private PowerGrid grid = null;
    private int power = 0;
    @Setter
    private int powerTarget = 16;

    private int time = 0;

    public final EnergyStorage feEnergy = new EnergyStorage(128000000);

    public AdjustablePowerConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("feEnergy", feEnergy.getEnergyStored());
        tag.putInt("power", power);
        tag.putInt("powerTarget", powerTarget);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        feEnergy.receiveEnergy(tag.getInt("feEnergy"), false);
        power = tag.getInt("power");
        powerTarget = tag.getInt("powerTarget");
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
        return Math.max(this.power, 0);
    }

    @Override
    public int getInputPower() {
        return this.power < 0 ? -this.power : 0;
    }

    @Override
    public PowerComponentType getComponentType() {
        return this.power >= 0 ? PowerComponentType.PRODUCER : PowerComponentType.CONSUMER;
    }

    @Override
    public int getRange() {
        return 2;
    }

    public void clientTick() {
        time += 1;
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;

        int prevPower = power;
        if (powerTarget >= 0) {
            fe2kw();
        } else {
            kw2fe();
        }
        if (prevPower != power) {
            grid.markChanged();
        }
    }

    private void fe2kw() {
        power = 0;
        int feConverted = powerTarget * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;
        if (feEnergy.getEnergyStored() < feConverted) return;

        feEnergy.extractEnergy(feConverted, false);
        power = powerTarget;
    }

    private void kw2fe() {
        power = powerTarget;
        if (grid == null || !grid.isWorking()) return;

        // 如果存储满了，停止消耗电网能量
        if (feEnergy.getEnergyStored() == feEnergy.getMaxEnergyStored()) {
            power = 0;
            return;
        }

        int feConverted = -power * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;
        feEnergy.receiveEnergy(feConverted, false);
    }

    @Override
    public Component getDisplayName() {
        return AddonBlocks.ADJUSTABLE_POWER_CONVERTER.get().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (player.isSpectator()) return null;
        return new SliderMenu(i, this::setPowerTarget);
    }

    public EnergyStorage getEnergyStorage() {
        return this.feEnergy;
    }
}