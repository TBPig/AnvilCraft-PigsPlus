package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.AutoRoyalSmithingMenu;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.dubhe.anvilcraft.inventory.RoyalGrindstoneMenu.REPAIR_COST_RECIPES;

@Getter
public class AutoRoyalSmithingTableBlockEntity extends AutoMachineBlockEntity {
    @Getter
    private @Nullable ItemStack resultStack = null;

    public AutoRoyalSmithingTableBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 3);
    }

    @Override
    protected ItemStackHandler createItemHandler(int size) {
        return new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                resultStack = getResult();
                setChanged();
            }
        };
    }

    @Override
    public void calcResult() {
        if (level == null) {
            resultStack = ItemStack.EMPTY;
            return;
        }

        SmithingRecipeInput input = new SmithingRecipeInput(
            itemHandler.getStackInSlot(0),
            itemHandler.getStackInSlot(1),
            itemHandler.getStackInSlot(2)
        );
        List<RecipeHolder<SmithingRecipe>> recipes = level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, input, level);
        if (recipes.isEmpty()) {
            resultStack = ItemStack.EMPTY;
            return;
        }

        RecipeHolder<SmithingRecipe> recipeholder = recipes.getFirst();
        ItemStack itemstack = recipeholder.value().assemble(input, level.registryAccess());
        if (!itemstack.isItemEnabled(level.enabledFeatures())) {
            resultStack = ItemStack.EMPTY;
            return;
        }

        resultStack = itemstack;
    }

    public ItemStack getResult() {
        calcResult();
        return resultStack == null ? ItemStack.EMPTY : resultStack;
    }

    @Override
    protected boolean work(Level level) {
        if (getGrid() == null || !getGrid().isWorking()) return false;

        calcResult();
        if (resultStack == null || resultStack.isEmpty()) return false;

        if (!exportItem(resultStack)) return false;

        itemHandler.extractItem(1, 1, false);
        itemHandler.extractItem(2, 1, false);
        level.updateNeighborsAt(getBlockPos(), AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK.get());
        return true;
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.getBoolean("HasResultItemStack") && tag.contains("ResultItemStack")) {
            CompoundTag ct = tag.getCompound("ResultItemStack");
            resultStack = ct.contains("id") ? ItemStack.parse(provider, ct).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        boolean hasResultItemStack = resultStack != null && !resultStack.isEmpty();
        tag.putBoolean("HasResultItemStack", hasResultItemStack);
        if (hasResultItemStack) {
            CompoundTag item = (CompoundTag) this.resultStack.save(provider);
            tag.put("ResultItemStack", item);
        }
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
}