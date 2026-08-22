package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.lib.v2.util.InventoryUtil;
import dev.dubhe.anvilcraft.util.BlockMiningEffect;
import dev.dubhe.anvilcraft.util.BreakBlockUtil;
import dev.dubhe.anvilcraft.util.DevourUtil;
import dev.anvilcraft.pigsplus.init.AddonDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockBreakerStaffItem extends Item {
    public BlockBreakerStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    public static int tryBreakBlock(ServerLevel level, Player player, ItemStack staff, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!DevourUtil.shouldDevour(state)) return 0;

        @Nullable IItemHandler source = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);

        if (protectsContainers(staff) && (source != null || level.getBlockEntity(pos) instanceof LecternBlockEntity)) {
            return 0;
        }

        int durabilityCost = getDurabilityCost(state, level, pos);
        if (!player.getAbilities().instabuild) {
            dropItem(level, player, pos, source);
        }

        if (!(state.getBlock() instanceof DoublePlantBlock)) {
            state.getBlock().playerWillDestroy(level, pos, state, player);
        }
        return level.destroyBlock(pos, false) ? durabilityCost : 0;
    }

    public static boolean protectsContainers(ItemStack stack) {
        return stack.getOrDefault(AddonDataComponents.BLOCK_BREAKER_STAFF_PROTECT_CONTAINERS, true);
    }

    public static void setProtectsContainers(Player player, boolean protectContainers) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof BlockBreakerStaffItem)) return;
        stack.set(AddonDataComponents.BLOCK_BREAKER_STAFF_PROTECT_CONTAINERS, protectContainers);
    }

    private static int getDurabilityCost(BlockState state, ServerLevel level, BlockPos pos) {
        return 1 + Math.max(0, (int) Math.ceil(state.getDestroySpeed(level, pos)));
    }

    private static void dropItem(ServerLevel level, Player player, BlockPos pos, @Nullable IItemHandler source) {
        List<ItemStack> dropList = BreakBlockUtil.drop(level, pos, BlockMiningEffect.NORMAL);
        Inventory inventory = player.getInventory();
        for (ItemStack drop : dropList) {
            collectItem(level, pos, drop, inventory);
        }
        if (source != null && dropList.isEmpty()) {
            for (int slot = 0; slot < source.getSlots(); slot++) {
                ItemStack stack = source.getStackInSlot(slot);
                collectItem(level, pos, stack, inventory);
            }
        }
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lectern) {
            ItemStack bookStack = InventoryUtil.insertItem(inventory, lectern.getBook());
            lectern.setBook(bookStack);
            if (!bookStack.isEmpty()) {
                Block.popResource(level, pos, bookStack);
                lectern.setBook(ItemStack.EMPTY);
            }
        }
    }

    private static void collectItem(ServerLevel level, BlockPos pos, ItemStack drop, Inventory inventory) {
        if (drop.isEmpty()) return;
        ItemStack remaining = InventoryUtil.insertItem(inventory, drop);
        if (!remaining.isEmpty()) {
            Block.popResource(level, pos, remaining);
        }
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        TooltipContext context,
        List<Component> tooltipComponents,
        TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.block_breaker_staff",
            Component.keybind("key.anvilcraft.switch_tool_mode")
        ).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.block_breaker_staff.mode",
            Component.translatable(
                protectsContainers(stack)
                ? "tooltip.anvilcraft_pigsplus.block_breaker_staff.protecting"
                : "tooltip.anvilcraft_pigsplus.block_breaker_staff.not_protecting"
            )
        ).withStyle(ChatFormatting.GRAY));
    }
}
