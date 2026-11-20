package dev.anvilcraft.pigsplus.inventory;

import dev.anvilcraft.pigsplus.init.AddonMenuTypes;
import dev.anvilcraft.pigsplus.item.MutedSounds;
import dev.anvilcraft.pigsplus.item.PortableSilencerItem;
import dev.anvilcraft.pigsplus.network.PortableSilencerSyncPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

/**
 * 便携消音器的选择界面：无槽位，改动直接写到打开界面时持有的那只手上的物品组件。
 */
public class PortableSilencerMenu extends AbstractContainerMenu {
    private final Player player;
    private final @Nullable InteractionHand hand;

    public PortableSilencerMenu(int containerId, Inventory inventory) {
        this(AddonMenuTypes.PORTABLE_SILENCER.get(), containerId, inventory);
    }

    public PortableSilencerMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory) {
        super(menuType, containerId);
        this.player = inventory.player;
        this.hand = PortableSilencerItem.findHand(inventory.player);
    }

    @Nullable
    private ItemStack getSilencer() {
        if (this.hand == null) return null;
        ItemStack stack = this.player.getItemInHand(this.hand);
        return stack.getItem() instanceof PortableSilencerItem ? stack : null;
    }

    /**
     * 只有已注册的音效才会被写入，避免客户端伪造的 id 撑大物品数据。
     */
    public void addSound(ResourceLocation soundId) {
        if (!BuiltInRegistries.SOUND_EVENT.containsKey(soundId)) return;
        this.updateSounds(sounds -> sounds.withSound(soundId));
    }

    public void removeSound(ResourceLocation soundId) {
        this.updateSounds(sounds -> sounds.withoutSound(soundId));
    }

    private void updateSounds(UnaryOperator<MutedSounds> operator) {
        ItemStack stack = this.getSilencer();
        if (stack == null) return;
        MutedSounds current = MutedSounds.get(stack);
        MutedSounds updated = operator.apply(current);
        if (updated.equals(current)) return;
        MutedSounds.set(stack, updated);
        this.syncToClient(updated);
    }

    private void syncToClient(MutedSounds sounds) {
        if (!(this.player instanceof ServerPlayer serverPlayer)) return;
        PacketDistributor.sendToPlayer(serverPlayer, new PortableSilencerSyncPacket(sounds));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.getSilencer() != null;
    }
}
