package dev.anvilcraft.pigsplus.block.entity;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.AutoRoyalSmithingMenu;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
public class AutoRoyalSmithingTableBlockEntity extends AutoMachineBlockEntity {
    @Getter
    private ItemStack resultStack = ItemStack.EMPTY;

    public AutoRoyalSmithingTableBlockEntity(BlockEntityType<? extends BlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 3);
    }

    @Override
    protected ItemStacksResourceHandler createItemHandler(int size) {
        return new ItemStacksResourceHandler(size) {
            @Override
            protected void onContentsChanged(int index, ItemStack previousContents) {
                resultStack = getResult();
                setChanged();
            }
        };
    }

    private void calcResult() {
        if (level == null) {
            resultStack = ItemStack.EMPTY;
            return;
        }

        SmithingRecipeInput input = new SmithingRecipeInput(
            itemHandler.getResource(0).toStack(),
            itemHandler.getResource(1).toStack(),
            itemHandler.getResource(2).toStack()
        );
        Optional<RecipeHolder<SmithingRecipe>> foundRecipe;
        if (this.level instanceof ServerLevel serverLevel) {
            foundRecipe = serverLevel.recipeAccess().getRecipeFor(RecipeType.SMITHING, input, serverLevel);
        } else {
            foundRecipe = Optional.empty();
        }

        foundRecipe.ifPresentOrElse(
            recipe -> this.resultStack = recipe.value().assemble(input),
            () -> resultStack = ItemStack.EMPTY
        );
    }

    public ItemStack getResult() {
        calcResult();
        return resultStack;
    }

    @Override
    protected boolean work(Level level) {
        if (getGrid() == null || !getGrid().isWorking()) return false;

        calcResult();
        if (resultStack.isEmpty()) return false;

        if (!exportItem(resultStack)) return false;


        try (Transaction transaction = Transaction.openRoot()) {
            itemHandler.extract(itemHandler.getResource(1), 1, transaction);
            itemHandler.extract(itemHandler.getResource(2), 1, transaction);
            transaction.commit();
        }
        level.updateNeighborsAt(getBlockPos(), AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK.get());
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("HasResultItemStack", !this.resultStack.isEmpty());
        if (!this.resultStack.isEmpty()) {
            output.store("ResultItemStack", ItemStack.OPTIONAL_CODEC, this.resultStack);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (input.getBooleanOr("HasResultItemStack", false)) {
            this.resultStack = input.read("ResultItemStack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
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

    @Override
    public Block getBlock() {
        return AddonBlocks.AUTO_ROYAL_SMITHING_TABLE_BLOCK.get();
    }
}