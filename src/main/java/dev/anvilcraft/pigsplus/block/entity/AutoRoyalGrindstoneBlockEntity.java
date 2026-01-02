package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.block.AutoRoyalGrindstoneBlock;
import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.AutoRoyalGrindstoneMenu;
import dev.dubhe.anvilcraft.api.itemhandler.ItemHandlerUtil;
import dev.dubhe.anvilcraft.api.power.IPowerConsumer;
import dev.dubhe.anvilcraft.api.power.PowerGrid;
import dev.dubhe.anvilcraft.block.BatchCrafterBlock;
import dev.dubhe.anvilcraft.block.entity.BaseMachineBlockEntity;
import dev.dubhe.anvilcraft.init.block.ModBlocks;
import dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu.DEFAULT_REPAIR_MATERIAL;
import static dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu.GOLD_PER_CURSE;
import static dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu.REPAIR_COST_RECIPES;

@Getter
public class AutoRoyalGrindstoneBlockEntity extends BaseMachineBlockEntity implements IPowerConsumer {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    @Getter
    private final int inputPower = 16;
    @Setter
    @Getter
    private PowerGrid grid;
    private boolean poweredBefore = false;
    private int cooldown = 0;
    @Getter
    private ItemStack resultToolStack = ItemStack.EMPTY;
    @Getter
    private ItemStack resultMaterialStack = ItemStack.EMPTY;
    private int usedMaterialCount = 0;
    @Getter
    private final int id;
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 0 && REPAIR_COST_RECIPES.containsKey(stack.getItem())) return stack;
            if (slot == 1 && !REPAIR_COST_RECIPES.containsKey(stack.getItem())) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            calcResult();
            setChanged();
        }
    };

    public AutoRoyalGrindstoneBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        id = COUNTER.incrementAndGet();
    }

    public void calcResult() {
        resultToolStack = ItemStack.EMPTY;
        resultMaterialStack = ItemStack.EMPTY;
        usedMaterialCount = 0;
        if (level == null) return;

        ItemStack toolStack = itemHandler.getStackInSlot(0);
        ItemStack materialStack = itemHandler.getStackInSlot(1);

        if (toolStack.isEmpty() || materialStack.isEmpty()) return;

        RoyalGrindstoneMenu.RepairCostRecipeEntry recipe = REPAIR_COST_RECIPES.getOrDefault(materialStack.getItem(), null);
        if (recipe == null) return;

        // 计算附魔惩罚可消耗的金材料
        int repairCost = toolStack.getOrDefault(DataComponents.REPAIR_COST, 0);
        usedMaterialCount = Math.min(materialStack.getCount(), repairCost / recipe.count());
        resultToolStack = toolStack.copy();
        resultToolStack.setCount(1);
        int remainMaterialCount = materialStack.getCount();
        if (usedMaterialCount > 0) {
            int materialAbility = usedMaterialCount * recipe.count();
            int remainRepairCost = repairCost - materialAbility;
            remainMaterialCount -= usedMaterialCount;
            resultToolStack.set(DataComponents.REPAIR_COST, remainRepairCost);
            resultMaterialStack = recipe.item().getDefaultInstance();
            resultMaterialStack.setCount(usedMaterialCount);
        }

        // 计算诅咒附魔可消耗的金材料
        if (!materialStack.is(DEFAULT_REPAIR_MATERIAL)) return;
        if (remainMaterialCount < GOLD_PER_CURSE) return;
        DataComponentType<ItemEnchantments> enchantmentComponent =
            resultToolStack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
        ItemEnchantments enchantments = resultToolStack.get(enchantmentComponent);
        if (enchantments == null) return;

        ItemEnchantments.Mutable mutEnch = new ItemEnchantments.Mutable(enchantments);
        Iterator<Holder<Enchantment>> iterator = mutEnch.keySet().iterator();
        // 一个一个诅咒附魔
        while (iterator.hasNext() && remainMaterialCount >= GOLD_PER_CURSE) {
            Holder<Enchantment> curseEnchantment = iterator.next();
            if (!curseEnchantment.is(EnchantmentTags.CURSE)) continue;
            iterator.remove();
            usedMaterialCount += GOLD_PER_CURSE;
            remainMaterialCount -= GOLD_PER_CURSE;
        }
        resultMaterialStack = recipe.item().getDefaultInstance();
        resultMaterialStack.setCount(usedMaterialCount);
        resultToolStack.set(enchantmentComponent, mutEnch.toImmutable());
        if (resultToolStack.is(Items.ENCHANTED_BOOK) && !EnchantmentHelper.hasAnyEnchantments(resultToolStack)) {
            resultToolStack = resultToolStack.transmuteCopy(Items.BOOK);
        }
    }


    public void tick(Level level, BlockPos pos) {
        flushState(level, pos);
        BlockState state = level.getBlockState(pos);
        level.updateNeighbourForOutputSignal(pos, state.getBlock());
        boolean powered = state.getValue(AutoRoyalGrindstoneBlock.POWERED);
        // 红石信号上升沿且冷却完毕，尝试进行自动研磨
        cooldown = Math.max(0, cooldown - 1);
        if (powered && !poweredBefore && !level.isClientSide && this.cooldown == 0) {
            if (work(level)) cooldown = AnvilCraftPigsPlus.CONFIG.autoRoyalSmithingTableCooldown;
        }
        poweredBefore = powered;
    }

    private boolean work(Level level) {
        if (grid == null || !grid.isWorking()) return false;
        calcResult();
        if (resultToolStack.isEmpty()) return false;

        if (!exportItem()) return false;

        // 消耗输入物品
        if (!itemHandler.getStackInSlot(1).isEmpty()) {
            itemHandler.extractItem(1, usedMaterialCount, false);
        }
        if (!itemHandler.getStackInSlot(0).isEmpty()) {
            itemHandler.extractItem(0, 1, false);
        }

        level.updateNeighborsAt(getBlockPos(), AddonBlocks.AUTO_ROYAL_GRINDSTONE_BLOCK.get());
        return true;
    }

    private boolean exportItem() {
        Direction direction = getDirection();
        IItemHandler cap = Objects.requireNonNull(getLevel()).getCapability(
            Capabilities.ItemHandler.BLOCK,
            getBlockPos().relative(direction),
            direction.getOpposite()
        );
        if (cap != null) {
            // 尝试向容器插入物品
            ItemStack remained = ItemHandlerUtil.insertItem(cap, resultToolStack, true);
            if (!remained.isEmpty()) return false;
            ItemHandlerUtil.insertItem(cap, resultToolStack, false);

            // 尝试向容器插入物品
            remained = ItemHandlerUtil.insertItem(cap, resultMaterialStack, true);
            if (remained.isEmpty()) {
                ItemHandlerUtil.insertItem(cap, resultMaterialStack, false);
            } else {
                // 强制向世界喷出物品
                spawnItemEntity(resultMaterialStack);
            }
        } else {
            // 尝试向世界喷出物品
            Vec3 center = getBlockPos().relative(getDirection()).getCenter();
            AABB aabb = new AABB(center.add(-0.125, -0.125, -0.125), center.add(0.125, 0.125, 0.125));
            if (!getLevel().noCollision(aabb)) return false;

            spawnItemEntity(resultToolStack);
            spawnItemEntity(resultMaterialStack);
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
        if (tag.getBoolean("HasResultItemStack") && tag.contains("ResultItemStack")) {
            CompoundTag ct = tag.getCompound("ResultItemStack");
            resultToolStack =
                ct.contains("id") ? ItemStack.parse(provider, ct).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        }
        this.poweredBefore = tag.getBoolean("PoweredBefore");
        this.cooldown = tag.getInt("Cooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Inventory", this.itemHandler.serializeNBT(provider));
        boolean hasResultItemStack = resultToolStack != null && !resultToolStack.isEmpty();
        tag.putBoolean("HasResultItemStack", hasResultItemStack);
        if (hasResultItemStack) {
            CompoundTag item = (CompoundTag) this.resultToolStack.save(provider);
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
        return new AutoRoyalGrindstoneMenu(AddonMenuTypes.AUTO_ROYAL_GRINDSTONE.get(), i, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.anvilcraft_pigsplus.auto_royal_grindstone");
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public @Nullable Level getCurrentLevel() {
        return getLevel();
    }
}