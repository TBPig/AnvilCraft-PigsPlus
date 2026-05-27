package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.AutoJewelCraftingMenu;
import dev.dubhe.anvilcraft.init.recipe.ModRecipeTypes;
import dev.dubhe.anvilcraft.recipe.JewelCraftingRecipe;
import dev.dubhe.anvilcraft.recipe.sync.RecipesRecord;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
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
    protected ItemStacksResourceHandler createItemHandler(int size) {
        return new ItemStacksResourceHandler(size) {

            @Override
            public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
                if (mayPlace(index, resource.toStack(amount))) {
                    return super.insert(index, resource, amount, transaction);
                } else {
                    return 0;
                }
            }

            public boolean mayPlace(int slot, ItemStack stack) {
                // slot 0 是源物品槽位
                if (slot == 0) {
                    return RecipesRecord.RECIPES.byType(ModRecipeTypes.JEWEL_CRAFTING.get()).stream().anyMatch(holder -> holder.value().source().test(stack));
                }
                if (resultRecipe != null) {
                    int idx = slot - 1;
                    var mergedIngredients = resultRecipe.ingredients();
                    if (idx < mergedIngredients.size()) {
                        var entry = mergedIngredients.get(idx);
                        return entry.test(stack);
                    }
                }
                return false;
            }

            @Override
            protected void onContentsChanged(int slot, ItemStack stack) {
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

        ItemStack mainStack = itemHandler.getResource(0).toStack(itemHandler.getAmountAsInt(0));
        if (mainStack.isEmpty()) return;

        RecipeHolder<JewelCraftingRecipe> recipeHolder = RecipesRecord.RECIPES.byType(ModRecipeTypes.JEWEL_CRAFTING.get()).stream().filter(holder -> holder.value().source().test(mainStack)).findFirst().orElse(null);
        if (recipeHolder == null) return;

        resultRecipe = recipeHolder.value();
        List<ItemStack> materialStack = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            materialStack.add(itemHandler.getResource(i + 1).toStack(itemHandler.getAmountAsInt(i + 1)));
        }
        var input = new JewelCraftingRecipe.Input(mainStack, materialStack);
        if (!resultRecipe.matches(input, level)) return;
//        if (!resultRecipe.isSpecial() && level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING)) return;
        ItemStack result = resultRecipe.assemble(input);
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
        for (int i = 0; i < recipe.ingredients().size(); i++) {
            int idx = i + 1;
            if (!itemHandler.getResource(idx).isEmpty()) {
                try (Transaction root = Transaction.openRoot()) {
                    itemHandler.extract(idx, itemHandler.getResource(idx), itemHandler.getAmountAsInt(idx), root);
                    root.commit();
                }
            }
        }

        level.updateNeighborsAt(getBlockPos(), AddonBlocks.AUTO_JEWEL_CRAFTING_TABLE_BLOCK.get());
        return true;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (input.getBooleanOr("HasResultItemStack", false)) {
            input.read("ResultItemStack", ItemStack.CODEC).ifPresent(it -> this.resultStack = it);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("HasResultItemStack", !resultStack.isEmpty());
        if (!resultStack.isEmpty()) {
            output.store("ResultItemStack", ItemStack.CODEC, resultStack);
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

    @Override
    public Block getBlock() {
        return AddonBlocks.AUTO_JEWEL_CRAFTING_TABLE_BLOCK.get();
    }
}