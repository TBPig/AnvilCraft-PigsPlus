package dev.anvilcraft.pigsplus.item;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.init.AddonDataComponents;
import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.inventory.GridAdapterMenu;
import dev.anvilcraft.pigsplus.network.GridAdapterInitPacket;
import dev.anvilcraft.pigsplus.wireless.GridAdapterManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class GridAdapterItem extends Item {
    public static final int INPUT_MODE = 1; // kW -> FE
    public static final int OUTPUT_MODE = 2; // FE -> kW
    public static final int DEFAULT_POWER = 16; // 16kW

    public GridAdapterItem(Properties properties) {
        super(properties);
    }

    public static int getMode(ItemStack stack) {
        return Math.clamp(
            stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.DEFAULT).value(),
            INPUT_MODE,
            OUTPUT_MODE
        );
    }

    public static void setMode(Player player, int mode) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof GridAdapterItem)) return;
        stack.set(
            DataComponents.CUSTOM_MODEL_DATA,
            new CustomModelData(Math.clamp(mode, INPUT_MODE, OUTPUT_MODE))
        );
    }

    public static int getMaxPower() {
        return Math.max(1, AnvilCraftPigsPlus.CONFIG.gridAdapterMaxPowerConversion);
    }

    public static int getPower(ItemStack stack) {
        return Math.clamp(
            stack.getOrDefault(AddonDataComponents.GRID_ADAPTER_POWER, DEFAULT_POWER),
            1,
            getMaxPower()
        );
    }

    public static void setPower(ItemStack stack, int power) {
        stack.set(
            AddonDataComponents.GRID_ADAPTER_POWER,
            Math.clamp(power, 1, getMaxPower())
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        Level level = context.getLevel();

        if (!player.isShiftKeyDown()) {
            openConfigScreen(level, player);
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            if (level.isClientSide()) return InteractionResult.sidedSuccess(true);
            if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.sidedSuccess(false);

            GridAdapterManager.toggle(
                serverLevel,
                context.getClickedPos(),
                context.getClickedFace(),
                getMode(context.getItemInHand()),
                getPower(context.getItemInHand()),
                player
            );
            return InteractionResult.sidedSuccess(false);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        openConfigScreen(level, player);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    private static void openConfigScreen(Level level, Player player) {
        if (level.isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof GridAdapterItem)) return;

        AddonMenuTypes.open(
            serverPlayer,
            new SimpleMenuProvider(
                (id, inventory, ignored) -> new GridAdapterMenu(id),
                Component.translatable("screen.anvilcraft_pigsplus.grid_adapter.title")
            )
        );
        PacketDistributor.sendToPlayer(
            serverPlayer,
            new GridAdapterInitPacket(getPower(stack), getMaxPower())
        );
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        TooltipContext context,
        List<Component> tooltipComponents,
        TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        int mode = getMode(stack);
        String modeKey = mode == OUTPUT_MODE
                         ? "screen.anvilcraft_pigsplus.grid_adapter.output"
                         : "screen.anvilcraft_pigsplus.grid_adapter.input";
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.grid_adapter.mode",
            Component.translatable(modeKey)
        ).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.grid_adapter.use",
            Component.keybind("key.anvilcraft.switch_tool_mode")
        ).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
            "tooltip.anvilcraft_pigsplus.grid_adapter.power",
            getPower(stack),
            getMaxPower()
        ).withStyle(ChatFormatting.GRAY));
    }
}
