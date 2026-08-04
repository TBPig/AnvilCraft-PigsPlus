package dev.anvilcraft.pigsplus.block.item;

import dev.anvilcraft.pigsplus.block.entity.WirelessTransmitterBlockEntity;
import dev.anvilcraft.pigsplus.init.AddonDataComponents;
import dev.anvilcraft.pigsplus.item.WirelessTransmitterData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class WirelessTransmitterBlockItem extends BlockItem {
    public WirelessTransmitterBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static boolean hasDataStored(ItemStack stack) {
        return stack.has(AddonDataComponents.WIRELESS_TRANSMITTER_TARGET);
    }

    public static BlockPos getData(ItemStack stack) {
        return Objects.requireNonNull(stack.get(AddonDataComponents.WIRELESS_TRANSMITTER_TARGET)).pos();
    }

    public static void setData(ItemStack stack, BlockPos pos) {
        stack.set(AddonDataComponents.WIRELESS_TRANSMITTER_TARGET, new WirelessTransmitterData(pos.immutable()));
    }

    public static void deleteData(ItemStack stack) {
        stack.remove(AddonDataComponents.WIRELESS_TRANSMITTER_TARGET);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        boolean container = level.getCapability(Capabilities.ItemHandler.BLOCK, clickedPos, clickedFace) != null
                            || level.getCapability(Capabilities.FluidHandler.BLOCK, clickedPos, clickedFace) != null;
        if (!hasDataStored(stack) || container) {
            InteractionResult result = this.useOn(context);
            return result == InteractionResult.PASS ? InteractionResult.FAIL : result;
        }
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasDataStored(stack);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(
        BlockPos pos,
        Level level,
        @Nullable Player player,
        ItemStack stack,
        BlockState state
    ) {
        if (level.isClientSide()) return false;
        if (!hasDataStored(stack)) {
            if (player != null) {
                player.displayClientMessage(
                    Component.translatable("message.anvilcraft_pigsplus.wireless_transmitter.placement_no_pos")
                        .withStyle(ChatFormatting.RED),
                    true
                );
            }
            return false;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof WirelessTransmitterBlockEntity transmitter) {
            BlockPos target = getData(stack);
            int result = transmitter.setTargetPos(target);
            if (result != WirelessTransmitterBlockEntity.TARGET_VALID) {
                showTargetFailure(player, result);
                return false;
            }
            return true;
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            deleteData(stack);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (level.getBlockState(clickedPos).is(this.getBlock())) {
            if (level.getBlockEntity(clickedPos) instanceof WirelessTransmitterBlockEntity transmitter) {
                BlockPos target = transmitter.getTargetPos();
                if (target != null) {
                    setData(stack, target);
                } else if (hasDataStored(stack)) {
                    int result = transmitter.setTargetPos(getData(stack));
                    if (result != WirelessTransmitterBlockEntity.TARGET_VALID && !level.isClientSide()) {
                        showTargetFailure(player, result);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (!hasDataStored(stack)) {
            setData(stack, clickedPos);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useOn(context);
    }

    private static void showTargetFailure(@Nullable Player player, int result) {
        if (player == null) return;
        String key = switch (result) {
            case WirelessTransmitterBlockEntity.TARGET_TOO_FAR ->
                "message.anvilcraft_pigsplus.wireless_transmitter.target_too_far";
            case WirelessTransmitterBlockEntity.TARGET_NOT_LOADED ->
                "message.anvilcraft_pigsplus.wireless_transmitter.target_not_loaded";
            case WirelessTransmitterBlockEntity.TARGET_IS_AIR ->
                "message.anvilcraft_pigsplus.wireless_transmitter.target_is_air";
            case WirelessTransmitterBlockEntity.TARGET_IS_SELF ->
                "message.anvilcraft_pigsplus.wireless_transmitter.target_is_self";
            default -> "message.anvilcraft_pigsplus.wireless_transmitter.target_invalid";
        };
        player.displayClientMessage(
            Component.translatable(key).withStyle(ChatFormatting.RED),
            true
        );
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        TooltipContext context,
        List<Component> tooltipComponents,
        TooltipFlag isAdvanced
    ) {
        super.appendHoverText(stack, context, tooltipComponents, isAdvanced);
        if (hasDataStored(stack)) {
            BlockPos pos = getData(stack);
            tooltipComponents.add(Component.translatable(
                "item.anvilcraft_pigsplus.wireless_transmitter.pos_set",
                pos.toShortString()
            ).withStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        }
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.wireless_transmitter.use"
        ).withStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
    }
}
