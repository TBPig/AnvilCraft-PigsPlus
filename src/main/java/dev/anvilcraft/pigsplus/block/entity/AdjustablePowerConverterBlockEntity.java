package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.IPowerProducer;
import dev.dubhe.anvilcraft.api.power.PowerComponentInfo;
import dev.dubhe.anvilcraft.api.power.PowerComponentType;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.inventory.SliderMenu;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

@Getter
public class AdjustablePowerConverterBlockEntity extends BlockEntity
    implements IPowerConsumer, IPowerProducer, MenuProvider {
    private @Nullable PowerGrid grid = null;
    private int power = 0;
    private int powerTarget = 16;
    @Setter
    private int cooldown = 40;
    private int time = 0;

    public final SimpleEnergyHandler feEnergy = new SimpleEnergyHandler(128000000);

    public AdjustablePowerConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        feEnergy.serialize(output.child("feEnergy"));
        output.putInt("power", power);
        output.putInt("powerTarget", powerTarget);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        feEnergy.deserialize(input.childOrEmpty("feEnergy"));
        power = input.getIntOr("power", 0);
        powerTarget = input.getIntOr("powerTarget", 16);
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
    public PowerComponentInfo toPowerComponentInfo() {
        return IPowerConsumer.super.toPowerComponentInfo();
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

        setChanged();
        int prevPower = power;
        if (powerTarget >= 0) {
            fe2kw();
        } else {
            kw2fe();
            fe_output();
        }
        if (prevPower != power && grid != null) {
            grid.markChanged();
        }
    }

    private void fe_output() {
        if (level == null) return;
        // 向每个方向输出能量
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = getBlockPos().relative(direction);
            BlockEntity adjacentBlockEntity = level.getBlockEntity(adjacentPos);
            if (adjacentBlockEntity == null) continue;

            EnergyHandler energyHandler = level.getCapability(Capabilities.Energy.BLOCK, adjacentPos, direction.getOpposite());
            if (energyHandler == null) continue;

            try (Transaction transaction = Transaction.openRoot()) {
                int receiveEnergy = energyHandler.insert(feEnergy.getAmountAsInt(), transaction);
                feEnergy.extract(receiveEnergy, transaction);
                transaction.commit();
            }

            if (feEnergy.getAmountAsInt() <= 0) break;
        }

    }

    private void fe2kw() {
        power = 0;
        int feConverted = powerTarget * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;
        if (feEnergy.getAmountAsInt() < feConverted) return;

        try (Transaction transaction = Transaction.openRoot()) {
            feEnergy.extract(feConverted, transaction);
            transaction.commit();
        }
        power = powerTarget;
    }

    private void kw2fe() {
        power = powerTarget;
        if (grid == null || !grid.isWorking()) return;

        // 如果存储满了，停止消耗电网能量
        if (feEnergy.getAmountAsInt() == feEnergy.getCapacityAsInt()) {
            power = 0;
            return;
        }

        // 没有这个代码，就会虚空生电一小段时间
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        int feConverted = -power * AnvilCraft.CONFIG.powerConverter.powerConverterEfficiency;

        try (Transaction transaction = Transaction.openRoot()) {
            feEnergy.insert(feConverted, transaction);
            transaction.commit();
        }
    }

    @Override
    public Component getDisplayName() {
        return AddonBlocks.ADJUSTABLE_POWER_CONVERTER.get().getName();
    }

    public void setTarget(int powerTarget) {
        this.powerTarget = powerTarget;
        this.cooldown = 40;
        setChanged();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (player.isSpectator()) return null;
        return new SliderMenu(i, this::setTarget);
    }
}