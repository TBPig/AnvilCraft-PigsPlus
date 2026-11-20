package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.client.gui.component.SilencerSoundButton;
import dev.anvilcraft.pigsplus.inventory.PortableSilencerMenu;
import dev.anvilcraft.pigsplus.item.MutedSounds;
import dev.anvilcraft.pigsplus.item.PortableSilencerItem;
import dev.anvilcraft.pigsplus.network.PortableSilencerAddSoundPacket;
import dev.anvilcraft.pigsplus.network.PortableSilencerRemoveSoundPacket;
import dev.dubhe.anvilcraft.constant.Constant;
import dev.dubhe.anvilcraft.constant.SharedTextures;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 便携消音器的声音选择界面：左侧为未屏蔽声音，右侧为已屏蔽声音。
 */
public class PortableSilencerScreen extends AbstractContainerScreen<PortableSilencerMenu> {
    public static final ResourceLocation BACKGROUND = SharedTextures.bg("machine", "active_silencer");
    public static final ResourceLocation BUTTON_ADD = SharedTextures.textureGui("machine/active_silencer/button_add");
    public static final ResourceLocation BUTTON_REMOVE = SharedTextures.textureGui("machine/active_silencer/button_remove");
    public static final int SOUND_FILTERED = 0;
    public static final int SOUND_MUTED = 1;

    private static final int VISIBLE_ROWS = 8;
    private static final int ROW_HEIGHT = 15;
    private static final int START_LEFT_X = 6;
    private static final int START_RIGHT_X = 132;
    private static final int LIST_TOP_Y = 35;
    private static final int SCROLL_BAR_HEIGHT = 120;
    private static final int SCROLL_BAR_TOP_Y = 35;
    private static final int SCROLL_BAR_WIDTH = 5;
    private static final int SCROLLER_HEIGHT = 9;
    private static final int SCROLL_BAR_START_LEFT_X = 119;
    private static final int SCROLL_BAR_START_RIGHT_X = 245;
    private static final double RECORD_RANGE = 32.0;

    private final List<ResourceLocation> allSounds = new ArrayList<>();
    private final List<ResourceLocation> filteredSounds = new ArrayList<>();
    private final List<ResourceLocation> mutedSounds = new ArrayList<>();
    private final Set<ResourceLocation> mutedSoundIds = new LinkedHashSet<>();
    private final Map<ResourceLocation, Integer> soundCounts = new HashMap<>();

    private @Nullable EditBox searchBox;
    @Getter
    private String filterText = "";
    private int leftScrollOff;
    private int rightScrollOff;
    private boolean draggingLeft;
    private boolean draggingRight;
    private Comparator<ResourceLocation> soundOrder = Comparator.comparing(ResourceLocation::toString);

    public PortableSilencerScreen(PortableSilencerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = Constant.SCREEN_TITLE_Y;
        this.collectAllSounds();

        int buttonTop = this.topPos + LIST_TOP_Y;
        for (int index = 0; index < VISIBLE_ROWS; index++) {
            this.addRenderableWidget(new SilencerSoundButton(
                this.leftPos + START_LEFT_X,
                buttonTop,
                index,
                SOUND_FILTERED,
                button -> {
                    if (button instanceof SilencerSoundButton soundButton) {
                        this.addMutedSound(soundButton.getIndex());
                    }
                },
                this,
                BUTTON_ADD
            ));
            buttonTop += ROW_HEIGHT;
        }

        buttonTop = this.topPos + LIST_TOP_Y;
        for (int index = 0; index < VISIBLE_ROWS; index++) {
            this.addRenderableWidget(new SilencerSoundButton(
                this.leftPos + START_RIGHT_X,
                buttonTop,
                index,
                SOUND_MUTED,
                button -> {
                    if (button instanceof SilencerSoundButton soundButton) {
                        this.removeMutedSound(soundButton.getIndex());
                    }
                },
                this,
                BUTTON_REMOVE
            ));
            buttonTop += ROW_HEIGHT;
        }

        EditBox searchBox = this.addRenderableWidget(new EditBox(
            Objects.requireNonNull(this.minecraft).font,
            this.leftPos + 78,
            this.topPos + 19,
            100,
            12,
            Component.translatable("screen.anvilcraft_pigsplus.portable_silencer.search")
        ));
        searchBox.setResponder(this::onSearchTextChange);
        searchBox.setValue(this.filterText);
        this.searchBox = searchBox;
        this.addRenderableWidget(Button.builder(
            Component.translatable("screen.anvilcraft_pigsplus.portable_silencer.refresh"),
            button -> this.refreshSoundOrder()
        ).bounds(this.leftPos + 78, this.topPos + 170, 100, 20).build());
        this.applyHeldMutedSounds();
        this.refreshLists();
    }

    /**
     * 客户端在服务端同步包到达前先用手持物品上的数据，避免开屏瞬间右侧列表为空。
     */
    private void applyHeldMutedSounds() {
        Player player = Objects.requireNonNull(this.minecraft).player;
        if (player == null) return;
        InteractionHand hand = PortableSilencerItem.findHand(player);
        if (hand == null) return;
        this.mutedSoundIds.addAll(MutedSounds.get(player.getItemInHand(hand)).sounds());
    }

    private void collectAllSounds() {
        this.allSounds.clear();
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        BuiltInRegistries.SOUND_EVENT.stream()
            .map(SoundEvent::getLocation)
            .filter(location -> {
                WeighedSoundEvents events = manager.getSoundEvent(location);
                return events != null && events.getSubtitle() != null;
            })
            .forEach(this.allSounds::add);
        this.allSounds.sort(this.soundOrder);
    }

    /**
     * 与主动消音器一致：统计只发生在界面打开期间，关闭界面即丢弃本次结果。
     * 距离由玩家自身位置给出，因此这里只按到玩家的距离过滤。
     */
    public void recordSound(ResourceLocation sound, Vec3 pos) {
        Player player = Objects.requireNonNull(this.minecraft).player;
        if (player == null) return;
        if (player.position().distanceToSqr(pos) > RECORD_RANGE * RECORD_RANGE) return;
        this.soundCounts.merge(sound, 1, Integer::sum);
    }

    /**
     * 按本次界面打开期间统计到的播放次数重新排序，让常听到的声音排在两侧列表前面。
     */
    private void refreshSoundOrder() {
        Map<ResourceLocation, Integer> counts = Map.copyOf(this.soundCounts);
        this.soundOrder = Comparator.<ResourceLocation>comparingInt(sound -> counts.getOrDefault(sound, 0))
            .reversed()
            .thenComparing(ResourceLocation::toString);
        this.allSounds.sort(this.soundOrder);
        this.leftScrollOff = 0;
        this.rightScrollOff = 0;
        this.refreshLists();
    }

    public int getPlayCount(ResourceLocation sound) {
        return this.soundCounts.getOrDefault(sound, 0);
    }

    private @Nullable Component subtitleOf(ResourceLocation sound) {
        WeighedSoundEvents events = Minecraft.getInstance().getSoundManager().getSoundEvent(sound);
        if (events == null) return null;
        return events.getSubtitle();
    }

    private void onSearchTextChange(@Nullable String text) {
        this.filterText = (text == null) ? "" : text;
        this.leftScrollOff = 0;
        this.refreshLists();
    }

    private void refreshLists() {
        this.filteredSounds.clear();
        this.mutedSounds.clear();
        this.mutedSounds.addAll(this.mutedSoundIds);
        this.mutedSounds.sort(this.soundOrder);

        for (ResourceLocation sound : this.allSounds) {
            if (this.mutedSoundIds.contains(sound)) continue;
            if (this.matchesSearch(sound)) {
                this.filteredSounds.add(sound);
            }
        }

        this.leftScrollOff = Mth.clamp(this.leftScrollOff, 0, Math.max(0, this.filteredSounds.size() - VISIBLE_ROWS));
        this.rightScrollOff = Mth.clamp(this.rightScrollOff, 0, Math.max(0, this.mutedSounds.size() - VISIBLE_ROWS));
    }

    private boolean matchesSearch(ResourceLocation sound) {
        String text = this.filterText;
        if (text.isEmpty()) return true;
        if (text.startsWith("#")) {
            return sound.toString().contains(text.substring(1));
        }
        if (text.startsWith("~")) {
            try {
                return Pattern.compile(text.substring(1)).matcher(sound.toString()).matches();
            } catch (Exception ignored) {
                return false;
            }
        }
        Component subtitle = this.subtitleOf(sound);
        return subtitle != null && subtitle.getString().contains(text);
    }

    private void addMutedSound(int index) {
        int actualIndex = index + this.leftScrollOff;
        if (actualIndex < 0 || actualIndex >= this.filteredSounds.size()) return;
        ResourceLocation sound = this.filteredSounds.get(actualIndex);
        this.mutedSoundIds.add(sound);
        PacketDistributor.sendToServer(new PortableSilencerAddSoundPacket(sound));
        this.refreshLists();
    }

    private void removeMutedSound(int index) {
        int actualIndex = index + this.rightScrollOff;
        if (actualIndex < 0 || actualIndex >= this.mutedSounds.size()) return;
        ResourceLocation sound = this.mutedSounds.get(actualIndex);
        this.mutedSoundIds.remove(sound);
        PacketDistributor.sendToServer(new PortableSilencerRemoveSoundPacket(sound));
        this.refreshLists();
    }

    /**
     * 接收服务端确认后的已屏蔽集合，直接以服务端数据为准重建两侧列表。
     */
    public void handleSync(MutedSounds sounds) {
        this.mutedSoundIds.clear();
        this.mutedSoundIds.addAll(sounds.sounds());
        this.rightScrollOff = 0;
        this.refreshLists();
    }

    public Component getSoundTextAt(int index, int variant) {
        ResourceLocation sound = this.getSoundIdAt(index, variant);
        if (sound == null) return Component.empty();
        Component subtitle = this.subtitleOf(sound);
        return subtitle == null ? Component.literal(sound.toString()) : subtitle;
    }

    public @Nullable ResourceLocation getSoundIdAt(int index, int variant) {
        List<ResourceLocation> list = variant == SOUND_FILTERED ? this.filteredSounds : this.mutedSounds;
        int actualIndex = index + (variant == SOUND_FILTERED ? this.leftScrollOff : this.rightScrollOff);
        if (actualIndex < 0 || actualIndex >= list.size()) return null;
        return list.get(actualIndex);
    }

    private boolean mouseInList(double mouseX, double mouseY, int startX, int endX) {
        return mouseX >= this.leftPos + startX
            && mouseX <= this.leftPos + endX
            && mouseY >= this.topPos + SCROLL_BAR_TOP_Y
            && mouseY <= this.topPos + SCROLL_BAR_TOP_Y + SCROLL_BAR_HEIGHT;
    }

    private boolean mouseInSlider(double mouseX, double mouseY, int barX) {
        return mouseX >= this.leftPos + barX
            && mouseX <= this.leftPos + barX + SCROLL_BAR_WIDTH
            && mouseY >= this.topPos + SCROLL_BAR_TOP_Y
            && mouseY <= this.topPos + SCROLL_BAR_TOP_Y + SCROLL_BAR_HEIGHT;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.mouseInList(mouseX, mouseY, START_LEFT_X, SCROLL_BAR_START_LEFT_X + SCROLL_BAR_WIDTH)) {
            if (this.filteredSounds.size() > VISIBLE_ROWS) {
                this.leftScrollOff = (int) Mth.clamp(
                    this.leftScrollOff - scrollY,
                    0,
                    this.filteredSounds.size() - VISIBLE_ROWS
                );
            }
            return true;
        }
        if (this.mouseInList(mouseX, mouseY, START_RIGHT_X, SCROLL_BAR_START_RIGHT_X + SCROLL_BAR_WIDTH)) {
            if (this.mutedSounds.size() > VISIBLE_ROWS) {
                this.rightScrollOff = (int) Mth.clamp(
                    this.rightScrollOff - scrollY,
                    0,
                    this.mutedSounds.size() - VISIBLE_ROWS
                );
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        assert this.minecraft != null;
        if (this.searchBox != null && !this.searchBox.isMouseOver(mouseX, mouseY)) {
            this.searchBox.setFocused(false);
        }
        this.draggingLeft = false;
        this.draggingRight = false;
        if (this.filteredSounds.size() > VISIBLE_ROWS && this.mouseInSlider(mouseX, mouseY, SCROLL_BAR_START_LEFT_X)) {
            this.draggingLeft = true;
        }
        if (this.mutedSounds.size() > VISIBLE_ROWS && this.mouseInSlider(mouseX, mouseY, SCROLL_BAR_START_RIGHT_X)) {
            this.draggingRight = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingLeft) {
            this.leftScrollOff = this.scrollOffsetFromMouse(mouseY, this.filteredSounds.size());
            return true;
        }
        if (this.draggingRight) {
            this.rightScrollOff = this.scrollOffsetFromMouse(mouseY, this.mutedSounds.size());
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private int scrollOffsetFromMouse(double mouseY, int totalCount) {
        int top = this.topPos + SCROLL_BAR_TOP_Y;
        int dragMax = totalCount - VISIBLE_ROWS + 1;
        float scroll = (float) ((mouseY - top - 13.5F) / (SCROLL_BAR_HEIGHT - 27.0F));
        scroll = scroll * dragMax + 0.5F;
        return Mth.clamp((int) scroll, 0, Math.max(0, dragMax));
    }

    private void renderScroller(GuiGraphics guiGraphics, int barX, int totalCount, int scrollOff) {
        int x = this.leftPos + barX;
        int y = this.topPos + SCROLL_BAR_TOP_Y;
        int scrollY = y;
        if (totalCount > VISIBLE_ROWS) {
            int maxY = y + SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT;
            scrollY = (int) (y + (scrollOff / (float) totalCount) * SCROLL_BAR_HEIGHT);
            scrollY = Mth.clamp(scrollY, y, maxY);
        }
        guiGraphics.blit(SharedTextures.SMALL_SLIDER, x, scrollY, 0, 0, 5, 9, 10, 9);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderScroller(guiGraphics, SCROLL_BAR_START_LEFT_X, this.filteredSounds.size(), this.leftScrollOff);
        this.renderScroller(guiGraphics, SCROLL_BAR_START_RIGHT_X, this.mutedSounds.size(), this.rightScrollOff);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert this.minecraft != null;
        if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
