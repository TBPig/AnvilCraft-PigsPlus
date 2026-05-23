package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.AutoRoyalGrindstoneBlock;
import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.entity.BaseMachineBlockEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public abstract class AutoMachineBlockEntity extends BaseMachineBlockEntity implements IPowerConsumer {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    @Getter
    private final int inputPower = 16;
    @Setter
    @Getter
    @Nullable
    private PowerGrid grid;
    protected boolean poweredBefore = false;
    protected int cooldown = 0;
    @Getter
    private final int id;
    protected final ResourceHandler<ItemResource> itemHandler;

    protected AutoMachineBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState state, int inventorySize) {
        super(type, pos, state);
        id = COUNTER.incrementAndGet();
        this.itemHandler = createItemHandler(inventorySize);
    }

    protected abstract ResourceHandler<ItemResource> createItemHandler(int size);

    public abstract void calcResult();

    public void tick(Level level, BlockPos pos) {
        flushState(level, pos);
        BlockState state = level.getBlockState(pos);
        level.updateNeighbourForOutputSignal(pos, state.getBlock());
        boolean powered = state.getValue(BlockStateProperties.POWERED);
        // 红石信号上升沿且冷却完毕，尝试进行自动加工
        cooldown = Math.max(0, cooldown - 1);
        if (powered && !poweredBefore && !level.isClientSide() && this.cooldown == 0) {
            if (work(level)) cooldown = AnvilCraftPigsPlus.CONFIG.autoMachineCooldown;
        }
        poweredBefore = powered;
    }

    protected abstract boolean work(Level level);

    protected boolean exportItem(ItemStack result) {
        return exportItem(result, List.of());
    }

    protected boolean exportItem(ItemStack result, List<ItemStack> byproducts) {
        Direction direction = getDirection();
        IItemHandler cap = Objects.requireNonNull(getLevel()).getCapability(
            Capabilities.ItemHandler.BLOCK,
            getBlockPos().relative(direction),
            direction.getOpposite()
        );
        if (cap != null) {
            // 尝试向容器插入物品
            ItemStack remained = ItemHandlerUtil.insertItem(cap, result, true);
            if (!remained.isEmpty()) return false;

            ItemHandlerUtil.insertItem(cap, result, false);
            for (ItemStack byproduct : byproducts) {
                remained = ItemHandlerUtil.insertItem(cap, byproduct, true);
                if (remained.isEmpty()) {
                    ItemHandlerUtil.insertItem(cap, byproduct, false);
                } else {
                    // 强制向世界喷出物品
                    spawnItemEntity(byproduct);
                }
            }
        } else {
            // 尝试向世界喷出物品
            Vec3 center = getBlockPos().relative(direction).getCenter();
            AABB aabb = new AABB(center.add(-0.125, -0.125, -0.125), center.add(0.125, 0.125, 0.125));
            if (!getLevel().noCollision(aabb)) return false;

            spawnItemEntity(result);
            for (ItemStack byproduct : byproducts) {
                spawnItemEntity(byproduct);
            }
        }
        return true;
    }

    protected void spawnItemEntity(ItemStack stack) {
        if (stack.isEmpty()) return;
        Vec3 center = getBlockPos().relative(getDirection()).getCenter();
        Vector3f step = getDirection().step();
        Level level = this.getLevel();
        if (level == null) return;
        ItemEntity itemEntity =
            new ItemEntity(level, center.x, center.y, center.z, stack, 0.25 * step.x, 0.25 * step.y, 0.25 * step.z);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        itemHandler.deserializeNBT(provider, tag.getCompound("Inventory"));
        this.poweredBefore = tag.getBoolean("PoweredBefore");
        this.cooldown = tag.getInt("Cooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Inventory", this.itemHandler.serializeNBT(provider));
        tag.putBoolean("PoweredBefore", this.poweredBefore);
        tag.putInt("Cooldown", this.cooldown);
    }

    public int getRedstoneSignal() {
        /*输出等同于有物品的输入槽的数量的红石信号*/
        int strength = 0;
        for (int index = 0; index < itemHandler.getSlots(); index++) {
            ItemStack itemStack = itemHandler.getStackInSlot(index);
            if (itemStack.isEmpty()) continue;
            strength++;
        }
        return Mth.clamp(strength, 0, 15);
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }


    @Override
    public Direction getDirection() {
        if (this.level == null) return Direction.UP;
        BlockState state = this.level.getBlockState(this.getBlockPos());
        if (!state.is(getBlock())) return Direction.UP;
        return state.getValue(AutoRoyalGrindstoneBlock.FACING);
    }

    @Override
    public void setDirection(Direction direction) {
        BlockPos pos = this.getBlockPos();
        Level level = this.getLevel();
        if (null == level) return;
        BlockState state = level.getBlockState(pos);
        if (!state.is(getBlock())) return;
        level.setBlockAndUpdate(pos, state.setValue(AutoRoyalGrindstoneBlock.FACING, direction));
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        ItemHandlerUtil.dropAllToPos(this.itemHandler, this.level, this.getPos().getCenter());
    }

    public abstract Block getBlock();
}
