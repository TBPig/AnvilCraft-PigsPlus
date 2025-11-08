package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.block.AutoRoyalSmithingTableBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.AutoRoyalSmithingMenu;
import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.BatchCrafterBlock;
import dev.dubhe.anvilcraft.block.entity.BaseMachineBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.anvilcraft.pigsplus.AnvilCraftPigsPlus.CONFIG;

@Getter
public class AutoRoyalSmithingTableBlockEntity extends BaseMachineBlockEntity implements IPowerConsumer {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @Getter
    private final int inputPower = 16;
    @Setter
    @Getter
    private PowerGrid grid;
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            displayItemStack = getResult();
            setChanged();
        }
    };

    public ItemStack getResult() {
        if (level == null) return ItemStack.EMPTY;

        SmithingRecipeInput input = new SmithingRecipeInput(
            itemHandler.getStackInSlot(0),
            itemHandler.getStackInSlot(1),
            itemHandler.getStackInSlot(2)
        );
        List<RecipeHolder<SmithingRecipe>> recipes = level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, input, level);
        if (recipes.isEmpty()) return ItemStack.EMPTY;

        RecipeHolder<SmithingRecipe> recipeholder = recipes.getFirst();
        ItemStack itemstack = recipeholder.value().assemble(input, level.registryAccess());
        if (!itemstack.isItemEnabled(level.enabledFeatures())) return ItemStack.EMPTY;

        return itemstack;
    }

    @Getter
    private ItemStack displayItemStack = null;

    private boolean poweredBefore = false;
    private int cooldown = 0;

    @Getter
    private final int id;


    public AutoRoyalSmithingTableBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        id = COUNTER.incrementAndGet();
    }

    public void tick(Level level, BlockPos pos) {
        flushState(level, pos);
        BlockState state = level.getBlockState(pos);
        level.updateNeighbourForOutputSignal(pos, state.getBlock());
        boolean powered = state.getValue(AutoRoyalSmithingTableBlock.POWERED);
        // 红石信号上升沿且冷却完毕，尝试进行自动锻造
        cooldown = Math.max(0, cooldown - 1);
        if (powered && !poweredBefore && !level.isClientSide && this.cooldown == 0) {
            if (smithing(level)) cooldown = CONFIG.autoRoyalSmithingTableCooldown;
        }
        poweredBefore = powered;
    }

    private boolean smithing(Level level) {
        if (grid == null || !grid.isWorking()) return false;

        ItemStack result = getResult();
        if (result.isEmpty()) return false;

        if (!exportItem(result, getDirection())) return false;

        itemHandler.extractItem(1, 1, false);
        itemHandler.extractItem(2, 1, false);
        level.updateNeighborsAt(getBlockPos(), AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK.get());
        return true;
    }

    private boolean exportItem(ItemStack result, Direction direction) {
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
        } else {
            // 尝试向世界喷出物品
            Vec3 center = getBlockPos().relative(getDirection()).getCenter();
            AABB aabb = new AABB(center.add(-0.125, -0.125, -0.125), center.add(0.125, 0.125, 0.125));
            if (!getLevel().noCollision(aabb)) return false;

            spawnItemEntity(result);
        }
        return true;
    }

    private void spawnItemEntity(ItemStack stack) {
        Vec3 center = getBlockPos().relative(getDirection()).getCenter();
        Vector3f step = getDirection().step();
        Level level = this.getLevel();
        if (level == null) return;
        ItemEntity itemEntity =
            new ItemEntity(level, center.x, center.y, center.z, stack, 0.25 * step.x, 0.25 * step.y, 0.25 * step.z);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
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
        if (tag.getBoolean("HasDisplayItemStack") && tag.contains("ResultItemStack")) {
            CompoundTag ct = tag.getCompound("ResultItemStack");
            displayItemStack =
                ct.contains("id") ? ItemStack.parse(provider, ct).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        }
        this.poweredBefore = tag.getBoolean("PoweredBefore");
        this.cooldown = tag.getInt("Cooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Inventory", this.itemHandler.serializeNBT(provider));
        boolean hasDisplayItemStack = displayItemStack != null && !displayItemStack.isEmpty();
        tag.putBoolean("HasDisplayItemStack", hasDisplayItemStack);
        if (hasDisplayItemStack) {
            CompoundTag item = (CompoundTag) this.displayItemStack.save(provider);
            tag.put("ResultItemStack", item);
        }
        tag.putBoolean("PoweredBefore", this.poweredBefore);
        tag.putInt("Cooldown", this.cooldown);
    }

    @Override
    public Direction getDirection() {
        return Direction.UP;
    }

    @Override
    public void setDirection(Direction direction) {
        if (level == null) return;
        BlockPos pos = getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.BATCH_CRAFTER.get())) return;
        level.setBlockAndUpdate(pos, state.setValue(BatchCrafterBlock.FACING, direction));
    }


    public int getRedstoneSignal() {
        /*输出等同于有物品的输入槽的数量的红石信号*/
        int strength = 0;
        for (int index = 0; index < itemHandler.getSlots(); index++) {
            ItemStack itemStack = itemHandler.getStackInSlot(index);
            if (itemStack.isEmpty()) continue;
            strength++;
        }
        return strength;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (player.isSpectator()) return null;
        return new AutoRoyalSmithingMenu(AddonMenuTypes.AUTO_ROYAL_SMITHING.get(), i, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.anvilcraft_pigsplus.auto_royal_smithing_table");
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public Level getCurrentLevel() {
        return getLevel();
    }
}