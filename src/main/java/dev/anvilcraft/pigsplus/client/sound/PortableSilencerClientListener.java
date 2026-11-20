package dev.anvilcraft.pigsplus.client.sound;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.client.gui.screen.PortableSilencerScreen;
import dev.anvilcraft.pigsplus.integration.curios.CuriosCompat;
import dev.anvilcraft.pigsplus.item.MutedSounds;
import dev.dubhe.anvilcraft.api.sound.ISoundEventListener;
import dev.dubhe.anvilcraft.api.sound.SoundHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 把本地玩家携带的便携消音器的选择集合并成一份缓存，供 AnvilCraft 的静音管线查询；
 * 并在界面打开期间把身边播放的声音转交给界面统计。
 */
@EventBusSubscriber(modid = AnvilCraftPigsPlus.MOD_ID, value = Dist.CLIENT)
public final class PortableSilencerClientListener implements ISoundEventListener {
    private static final PortableSilencerClientListener INSTANCE = new PortableSilencerClientListener();

    private Set<ResourceLocation> mutedSounds = Set.of();

    private PortableSilencerClientListener() {
    }

    /**
     * 与主动消音器保持一致：只在界面打开时统计。每条通往声音引擎的声音都会经过这里，
     * 因此 {@code playLocalSound} 那条不经过 {@code PlayLevelSoundEvent} 的路径也不会漏掉。
     */
    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        SoundInstance sound = event.getSound();
        if (sound == null || sound.isRelative()) return;
        if (!(Minecraft.getInstance().screen instanceof PortableSilencerScreen screen)) return;
        screen.recordSound(sound.getLocation(), new Vec3(sound.getX(), sound.getY(), sound.getZ()));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        Player player = minecraft.player;
        if (level == null || player == null) {
            INSTANCE.mutedSounds = Set.of();
            return;
        }
        INSTANCE.refreshMutedSounds(player);
        INSTANCE.ensureRegistered(level);
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        INSTANCE.mutedSounds = Set.of();
    }

    /**
     * AnvilCraft 会在退出存档、服务器停止时清空监听器，因此每 tick 检查当前维度是否仍然注册。
     */
    private void ensureRegistered(ClientLevel level) {
        List<ISoundEventListener> listeners =
            SoundHelper.INSTANCE.getEventListeners().get(level.dimension());
        if (listeners != null && listeners.contains(INSTANCE)) return;
        SoundHelper.INSTANCE.register(level, INSTANCE);
    }

    private void refreshMutedSounds(Player player) {
        Set<ResourceLocation> collected = new HashSet<>();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            this.collectFrom(player.getInventory().getItem(slot), collected);
        }
        for (ItemStack stack : CuriosCompat.getEquippedItems(player)) {
            this.collectFrom(stack, collected);
        }
        this.mutedSounds = collected.isEmpty() ? Set.of() : collected;
    }

    /**
     * 只要物品带上了非空的屏蔽列表组件就参与静音，不限定物品种类。
     */
    private void collectFrom(ItemStack stack, Set<ResourceLocation> collected) {
        MutedSounds sounds = MutedSounds.get(stack);
        if (sounds.isEmpty()) return;
        collected.addAll(sounds.sounds());
    }

    /**
     * 被屏蔽的声音会在声音引擎入口处被取消，走不到 {@link #onPlaySound}，这里补记。
     * 只有判定为屏蔽时才会记录，因此与 {@link #onPlaySound} 互斥，不会重复计数。
     */
    @Override
    public boolean shouldMute(ResourceLocation sound, Vec3 pos) {
        if (!this.mutedSounds.contains(sound)) return false;
        if (Minecraft.getInstance().screen instanceof PortableSilencerScreen screen) {
            screen.recordSound(sound, pos);
        }
        return true;
    }
}
