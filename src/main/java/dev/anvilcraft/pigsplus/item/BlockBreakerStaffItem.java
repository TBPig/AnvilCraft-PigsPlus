package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.lib.v2.util.InventoryUtil;
import dev.anvilcraft.pigsplus.init.AddonDataComponents;
import dev.anvilcraft.pigsplus.util.BlockBreakerUtil;
import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.util.AnvilUtil;
import dev.dubhe.anvilcraft.util.BlockMiningEffect;
import dev.dubhe.anvilcraft.util.DevourUtil;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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
        if (protectsContainers(staff) && BlockBreakerUtil.isItemHandlerContainer(level, pos)) return 0;
        if (!DevourUtil.shouldDevour(level.getBlockState(pos))) return 0;

        BlockState state = level.getBlockState(pos);
        int durabilityCost = getDurabilityCost(state, level, pos);
        boolean collectDrops = !player.getAbilities().instabuild;
        BlockBreakerUtil.BreakResult result = BlockBreakerUtil.breakBlock(
            level,
            pos,
            BlockMiningEffect.NORMAL,
            player,
            AnvilCraft.CONFIG.world.blockDevourerProtectContainers,
            collectDrops
        );
        if (!result.destroyed()) return 0;

        if (collectDrops) {
            Inventory inventory = player.getInventory();
            for (ItemStack drop : result.drops()) {
                insertOrDrop(level, pos.getCenter(), inventory, drop);
            }
        }
        return durabilityCost;
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

    private static void insertOrDrop(ServerLevel level, Vec3 center, Inventory inventory, ItemStack drop) {
        if (drop.isEmpty()) return;
        ItemStack remaining = InventoryUtil.insertItem(inventory, drop);
        if (remaining.isEmpty()) return;
        AnvilUtil.dropItems(List.of(remaining), level, center);
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
