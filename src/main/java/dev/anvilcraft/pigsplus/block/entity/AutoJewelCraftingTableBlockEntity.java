package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.AutoJewelCraftingMenu;
import dev.dubhe.anvilcraft.recipe.JewelCraftingRecipe;
import dev.dubhe.anvilcraft.recipe.anvil.cache.RecipeCaches;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AutoJewelCraftingTableBlockEntity extends AutoMachineBlockEntity {
    @Getter
    private ItemStack resultStack = ItemStack.EMPTY;
    @Getter
    private @Nullable JewelCraftingRecipe resultRecipe = null;

    public AutoJewelCraftingTableBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 5);
    }

    @Override
    protected ItemStackHandler createItemHandler(int size) {
        return new ItemStackHandler(size) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (mayPlace(slot, stack)) {
                    return super.insertItem(slot, stack, simulate);
                } else {
                    return stack;
                }
            }

            public boolean mayPlace(int slot, ItemStack stack) {
                // slot 0 是源物品槽位
                if (slot == 0) {
                    return RecipeCaches.getAllJewelResultItem().contains(stack.getItem());
                }
                if (resultRecipe != null) {
                    int idx = slot - 1;
                    var mergedIngredients = resultRecipe.mergedIngredients;
                    if (idx < mergedIngredients.size()) {
                        var entry = mergedIngredients.get(idx);
                        Ingredient ingredient = entry.getKey();
                        return ingredient.test(stack);
                    }
                }
                return false;
            }

            @Override
            protected void onContentsChanged(int slot) {
                calcResult();
                setChanged();
            }
        };
    }

    @Override
    public void calcResult() {
        resultStack = ItemStack.EMPTY;
        resultRecipe = null;
        if (level == null) return;

        ItemStack mainStack = itemHandler.getStackInSlot(0);
        if (mainStack.isEmpty()) return;

        RecipeHolder<JewelCraftingRecipe> recipeHolder = RecipeCaches.getJewelRecipeByResult(mainStack);
        if (recipeHolder == null) return;

        resultRecipe = recipeHolder.value();
        List<ItemStack> materialStack = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            materialStack.add(itemHandler.getStackInSlot(i + 1));
        }
        var input = new JewelCraftingRecipe.Input(mainStack, materialStack);
        if (!resultRecipe.matches(input, level)) return;
        if (!resultRecipe.isSpecial() && level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING)) return;
        ItemStack result = resultRecipe.assemble(input, level.registryAccess());
        if (!result.isItemEnabled(level.enabledFeatures())) return;

        resultStack = result.copy();
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(level.registryAccess().holderOrThrow(Enchantments.VANISHING_CURSE), 1);
        resultStack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
    }

    @Override
    protected boolean work(Level level) {
        if (getGrid() == null || !getGrid().isWorking()) return false;

        calcResult();
        if (resultStack.isEmpty() || resultRecipe == null) return false;
        if (!exportItem(resultStack)) return false;

        // 消耗输入物品
        JewelCraftingRecipe recipe = this.resultRecipe;
        for (int i = 0; i < recipe.mergedIngredients.size(); i++) {
            int idx = i + 1;
            if (!itemHandler.getStackInSlot(idx).isEmpty()) {
                var entry = recipe.mergedIngredients.get(i);
                itemHandler.extractItem(idx, entry.getIntValue(), false);
            }
        }

        level.updateNeighborsAt(getBlockPos(), AddonBlocks.AUTO_JEWEL_CRAFTING_TABLE_BLOCK.get());
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
        return new AutoJewelCraftingMenu(AddonMenuTypes.AUTO_JEWEL_CRAFTING.get(), i, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.anvilcraft_pigsplus.auto_jewel_crafting_table");
    }
}