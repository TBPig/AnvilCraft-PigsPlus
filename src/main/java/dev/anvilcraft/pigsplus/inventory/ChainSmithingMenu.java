package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.init.AddonBlocks;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class ChainSmithingMenu extends ItemCombinerMenu {
    private final Level level;
    private final List<RecipeHolder<SmithingRecipe>> recipes;
    private final List<RecipeHolder<SmithingRecipe>> selectedRecipes;
    private final List<SmithingRecipeInput> recipeInputs;
    private final List<Integer> usedAdditionSlots;

    public static final int MAX = 4;


    public ChainSmithingMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public ChainSmithingMenu(MenuType<ChainSmithingMenu> type, int containerId, Inventory playerInventory) {
        this(type, containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public ChainSmithingMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        this(AddonMenuTypes.CHAIN_SMITHING.get(), containerId, playerInventory, access);
    }

    /**
     * 连锁锻造台菜单
     *
     * @param type            类型
     * @param containerId     容器id
     * @param playerInventory 背包
     * @param access          检查
     */
    public ChainSmithingMenu(
        MenuType<ChainSmithingMenu> type, int containerId, Inventory playerInventory, ContainerLevelAccess access
    ) {
        super(type, containerId, playerInventory, access);
        level = playerInventory.player.level();
        recipes = level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
        selectedRecipes = new ArrayList<>();
        recipeInputs = new ArrayList<>();
        usedAdditionSlots = new ArrayList<>();
    }

    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
            // 4个模板槽位 (0-3)
            .withSlot(
                0, 8, 29, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isTemplateIngredient(itemStack))
            )
            .withSlot(
                1, 27, 29, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isTemplateIngredient(itemStack))
            )
            .withSlot(
                2, 8, 48, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isTemplateIngredient(itemStack))
            )
            .withSlot(
                3, 27, 48, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isTemplateIngredient(itemStack))
            )
            // 1个基础物品槽位 (4)
            .withSlot(
                4, 60, 38, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isBaseIngredient(itemStack))
            )
            // 4个材料槽位 (5-8)
            .withSlot(
                5, 93, 29, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isAdditionIngredient(itemStack))
            )
            .withSlot(
                6, 112, 29, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isAdditionIngredient(itemStack))
            )
            .withSlot(
                7, 93, 48, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isAdditionIngredient(itemStack))
            )
            .withSlot(
                8, 112, 48, itemStack -> this.recipes.stream()
                    .anyMatch(smithingRecipe -> smithingRecipe.value().isAdditionIngredient(itemStack))
            )
            // 1个结果槽位 (9)
            .withResultSlot(9, 152, 38)
            .build();
    }

    protected boolean isValidBlock(BlockState state) {
        return state.is(AddonBlocks.CHAIN_SMITHING_TABLE_BLOCK.get());
    }

    protected boolean mayPickup(Player player, boolean hasStack) {
        if (selectedRecipes.isEmpty()) return false;
        for (int i = 0; i < selectedRecipes.size(); i++) {
            if (!selectedRecipes.get(i).value().matches(recipeInputs.get(i), this.level)) {
                return false;
            }
        }
        return true;
    }

    protected void onTake(Player player, ItemStack stack) {
        stack.onCraftedBy(player.level(), player, stack.getCount());
        this.resultSlots.awardUsedRecipes(player, this.getRelevantItems());

        List<Integer> usedAdditionSlots1 = this.usedAdditionSlots.stream().toList();

        // 不消耗模板，只消耗材料
        this.shrinkStackInSlot(4); // 基础物品槽位
        for (Integer usedAdditionSlot : usedAdditionSlots1) {
            this.shrinkStackInSlot(usedAdditionSlot);
        }
        this.access.execute((level, blockPos) -> level.levelEvent(1044, blockPos, 0));
    }

    private @Unmodifiable List<ItemStack> getRelevantItems() {
        return List.of(
            this.inputSlots.getItem(0),
            this.inputSlots.getItem(1),
            this.inputSlots.getItem(2),
            this.inputSlots.getItem(3),
            this.inputSlots.getItem(4),
            this.inputSlots.getItem(5),
            this.inputSlots.getItem(6),
            this.inputSlots.getItem(7),
            this.inputSlots.getItem(8)
        );
    }

    private void shrinkStackInSlot(int index) {
        ItemStack itemStack = this.inputSlots.getItem(index);
        if (!itemStack.isEmpty()) {
            itemStack.shrink(1);
            this.inputSlots.setItem(index, itemStack);
        }
    }

    @Override
    public void createResult() {
        // 清空之前的结果
        this.resultSlots.setItem(0, ItemStack.EMPTY);
        selectedRecipes.clear();
        recipeInputs.clear();
        if (!level.isClientSide) {
            usedAdditionSlots.clear();
        }

        // 检查是否有模板、基础物品和材料
        ItemStack baseItem = this.inputSlots.getItem(4);

        List<Integer> templateSlots = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (!this.inputSlots.getItem(i).isEmpty()) templateSlots.add(i);
        }

        List<Integer> additionSlots = new ArrayList<>();
        for (int i = 5; i < 9; i++) {
            if (!this.inputSlots.getItem(i).isEmpty()) additionSlots.add(i);
        }
        if (templateSlots.isEmpty() || baseItem.isEmpty() || additionSlots.isEmpty()) return;

        // 判断配方是否存在并更新槽位列表，如果存在便会添加配方并设置结果
        ItemStack resultItem = ItemStack.EMPTY;
        while (true) {
            ItemStack newItem = this.findAndRemoveUsedSlots(templateSlots, baseItem, additionSlots);
            if (newItem.isEmpty()) break;
            baseItem = newItem;
            resultItem = newItem;
        }
        // 设置最终结果
        if (!resultItem.isEmpty()) {
            this.resultSlots.setItem(0, resultItem);
        }
    }

    /**
     * 查找配方并从搜索槽位中移除已使用的槽位
     *
     * @param templateSlots 模板槽位列表
     * @param baseItem      基础物品
     * @param additionSlots 材料槽位列表
     * @return 合成结果，如果无匹配配方则返回空物品
     */
    private ItemStack findAndRemoveUsedSlots(List<Integer> templateSlots, ItemStack baseItem, List<Integer> additionSlots) {
        for (Integer templateSlot : templateSlots) {
            for (Integer additionSlot : additionSlots) {
                SmithingRecipeInput input =
                    new SmithingRecipeInput(this.inputSlots.getItem(templateSlot), baseItem, this.inputSlots.getItem(additionSlot));
                List<RecipeHolder<SmithingRecipe>> list =
                    this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, input, this.level);
                if (list.isEmpty()) continue;
                // 如果存在配方，则屏蔽已用材料，并将产物暂时设为结果并输出(有输出就会进行下一次循环)
                RecipeHolder<SmithingRecipe> recipeholder = list.getFirst();
                ItemStack itemstack = recipeholder.value().assemble(input, this.level.registryAccess());
                if (itemstack.isItemEnabled(this.level.enabledFeatures())) {
                    templateSlots.remove(templateSlot);
                    additionSlots.remove(additionSlot);
                    if (!level.isClientSide) {
                        usedAdditionSlots.add(additionSlot);
                    }
                    selectedRecipes.add(recipeholder);
                    recipeInputs.add(input);
                    resultSlots.setRecipeUsed(recipeholder);
                    return itemstack.copy();
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotToQuickMoveTo(ItemStack stack) {
        // 检查是否为模板材料
        if (this.recipes.stream().anyMatch(smithingRecipe -> smithingRecipe.value().isTemplateIngredient(stack))) {
            // 寻找第一个空的模板槽位 (0-3)
            for (int i = 0; i < 4; i++) {
                if (this.inputSlots.getItem(i).isEmpty()) {
                    return i;
                }
            }
        }
        // 检查是否为基础物品
        else if (this.recipes.stream().anyMatch(smithingRecipe -> smithingRecipe.value().isBaseIngredient(stack))) {
            return 4;
        }
        // 检查是否为添加材料
        else if (this.recipes.stream().anyMatch(smithingRecipe -> smithingRecipe.value().isAdditionIngredient(stack))) {
            // 寻找第一个空的材料槽位 (5-8)
            for (int i = 5; i < 9; i++) {
                if (this.inputSlots.getItem(i).isEmpty()) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean canMoveIntoInputSlots(ItemStack stack) {
        return this.recipes.stream()
            .anyMatch(smithingRecipe ->
                smithingRecipe.value().isTemplateIngredient(stack) ||
                smithingRecipe.value().isBaseIngredient(stack) ||
                smithingRecipe.value().isAdditionIngredient(stack));
    }
}