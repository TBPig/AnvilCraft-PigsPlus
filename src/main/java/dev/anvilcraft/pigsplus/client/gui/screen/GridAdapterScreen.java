package dev.anvilcraft.pigsplus.client.gui.screen;

import dev.anvilcraft.pigsplus.AnvilCraftPigsPlus;
import dev.anvilcraft.pigsplus.inventory.GridAdapterMenu;
import dev.anvilcraft.pigsplus.network.GridAdapterUpdatePacket;
import dev.dubhe.anvilcraft.client.gui.component.Slider;
import dev.dubhe.anvilcraft.client.gui.component.TexturedButton;
import dev.dubhe.anvilcraft.constant.Constant;
import dev.dubhe.anvilcraft.constant.SharedTextures;
import dev.dubhe.anvilcraft.util.Callback;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GridAdapterScreen extends AbstractContainerScreen<GridAdapterMenu> {
    public static final ResourceLocation BACKGROUND =
        AnvilCraftPigsPlus.of("textures/gui/container/background/grid_adapter.png");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 77;
    public static final ResourceLocation BUTTON_MIN = SharedTextures.textureGui("misc/slider_like/button_min");
    public static final ResourceLocation BUTTON_MINUS = SharedTextures.textureGui("misc/slider_like/button_minus");
    public static final ResourceLocation BUTTON_ADD = SharedTextures.textureGui("misc/slider_like/button_add");
    public static final ResourceLocation BUTTON_MAX = SharedTextures.textureGui("misc/slider_like/button_max");

    private @Nullable EditBox valueInput;
    private @Nullable GridAdapterSlider slider;
    private List<Integer> sliderValues = List.of(0);
    private int lastValidValue;
    private int maxPower;

    public GridAdapterScreen(GridAdapterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.maxPower = Math.max(1, this.maxPower);
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = Constant.SCREEN_TITLE_Y;

        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;

        this.sliderValues = createSliderValues(this.maxPower);
        this.slider = new GridAdapterSlider(
            8 + offsetX,
            31 + offsetY,
            144,
            this.sliderValues,
            this::setValueFromSlider
        );
        this.slider.setValues(this.sliderValues, this.lastValidValue);
        this.addRenderableWidget(this.slider);

        this.addRenderableWidget(new TexturedButton(
            8 + offsetX,
            43 + offsetY,
            16,
            16,
            BUTTON_MIN,
            16,
            16,
            32,
            button -> this.setValueFromButton(1)
        ));
        this.addRenderableWidget(new TexturedButton(
            26 + offsetX,
            43 + offsetY,
            16,
            16,
            BUTTON_MINUS,
            16,
            16,
            32,
            button -> this.stepSlider(-1)
        ));
        this.addRenderableWidget(new TexturedButton(
            134 + offsetX,
            43 + offsetY,
            16,
            16,
            BUTTON_ADD,
            16,
            16,
            32,
            button -> this.stepSlider(1)
        ));
        this.addRenderableWidget(new TexturedButton(
            152 + offsetX,
            43 + offsetY,
            16,
            16,
            BUTTON_MAX,
            16,
            16,
            32,
            button -> this.setValueFromButton(this.maxPower)
        ));

        this.valueInput = new EditBox(this.font, offsetX + 50, offsetY + 47, 76, 8, Component.literal("value"));
        this.valueInput.setCanLoseFocus(true);
        this.valueInput.setTextColor(-1);
        this.valueInput.setTextColorUneditable(-1);
        this.valueInput.setBordered(false);
        this.valueInput.setMaxLength(12);
        this.valueInput.setValue(String.valueOf(this.lastValidValue));
        this.valueInput.setResponder(this::onValueChanged);
        this.addRenderableWidget(this.valueInput);
        this.setInitialFocus(this.valueInput);
    }

    public void initFromServer(int value, int max) {
        this.maxPower = Math.max(1, max);
        this.lastValidValue = Math.clamp(value, 1, this.maxPower);
        this.sliderValues = createSliderValues(this.maxPower);
        if (this.slider != null) {
            this.slider.setValues(this.sliderValues, this.lastValidValue);
        }
        if (this.valueInput != null) {
            this.valueInput.setValue(String.valueOf(this.lastValidValue));
        }
    }

    public void setValueFromButton(int value) {
        this.setValueFromSlider(Math.clamp(value, 1, this.maxPower));
    }

    private void stepSlider(int step) {
        if (this.slider == null) return;
        this.slider.step(step);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.slider != null) {
            this.slider.onClick(mouseX, mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.slider != null) {
            this.slider.onDrag(mouseX, mouseY, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.slider != null) {
            this.slider.onReleased();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void setValueFromSlider(int value) {
        this.lastValidValue = value;
        if (this.valueInput != null) {
            this.valueInput.setValue(String.valueOf(value));
        } else {
            PacketDistributor.sendToServer(new GridAdapterUpdatePacket(value));
        }
    }

    private void onValueChanged(String value) {
        if (value.isEmpty()) return;
        if (!value.matches("^[0-9]+$")) {
            if (this.valueInput != null) {
                this.valueInput.setValue(String.valueOf(this.lastValidValue));
            }
            return;
        }
        long parsed;
        try {
            parsed = Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return;
        }
        int clamped = (int) Math.min(parsed, this.maxPower);
        if (clamped < 1) {
            if (this.valueInput != null) {
                this.valueInput.setValue("1");
            }
            return;
        }
        if (parsed != clamped) {
            if (this.valueInput != null) {
                this.valueInput.setValue(String.valueOf(clamped));
            }
            return;
        }
        this.lastValidValue = clamped;
        if (this.slider != null) {
            this.slider.setValues(this.sliderValues, clamped);
        }
        if (this.valueInput != null) {
            PacketDistributor.sendToServer(new GridAdapterUpdatePacket(clamped));
        }
    }

    private static List<Integer> createSliderValues(int maxPower) {
        List<Integer> values = new ArrayList<>();
        int[] mantissas = {1, 4, 16, 64, 256};
        for (long scale = 1L; scale <= maxPower; scale *= 1000L) {
            for (int mantissa : mantissas) {
                long value = mantissa * scale;
                if (value > maxPower) break;
                values.add((int) value);
            }
        }
        if (values.isEmpty()) {
            values.add(1);
        }
        return List.copyOf(values);
    }

    private static String formatPower(int value) {
        if (value < 1000) return String.valueOf(value);
        long unitValue = 1000L;
        int unitIndex = 0;
        String[] units = {"k", "M", "G", "T"};
        while (unitValue * 1000L <= value && unitIndex < units.length - 1) {
            unitValue *= 1000L;
            unitIndex++;
        }
        double scaled = (double) value / unitValue;
        String number;
        if (scaled >= 100.0) {
            number = String.format(Locale.ROOT, "%.0f", scaled);
        } else if (scaled >= 10.0) {
            number = trimDecimal(String.format(Locale.ROOT, "%.1f", scaled));
        } else {
            number = trimDecimal(String.format(Locale.ROOT, "%.2f", scaled));
        }
        return number + units[unitIndex];
    }

    private static String trimDecimal(String value) {
        if (!value.contains(".")) return value;
        return value.replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
        guiGraphics.drawString(
            this.font,
            Component.translatable(
                "screen.anvilcraft_pigsplus.grid_adapter.value",
                formatPower(this.lastValidValue),
                formatPower(this.maxPower)
            ),
            30,
            16,
            0xFF404040,
            false
        );
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    private static final class GridAdapterSlider extends Slider {
        private List<Integer> values;

        private GridAdapterSlider(int x, int y, int length, List<Integer> values, Callback<Integer> callback) {
            super(x, y, 0, Math.max(1, values.size() - 1), length, callback);
            this.values = values;
        }

        private void setValues(List<Integer> values, int selected) {
            this.values = values;
            this.setValue(selected);
        }

        private int nearestIndex(int selected) {
            int index = 0;
            int bestDistance = Integer.MAX_VALUE;
            for (int i = 0; i < this.values.size(); i++) {
                int distance = Math.abs(this.values.get(i) - selected);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    index = i;
                }
            }
            return index;
        }

        private int getIndex() {
            if (this.values.size() <= 1) return 0;
            return (int) Math.round(this.getProportion() * (this.values.size() - 1));
        }

        @Override
        public int getValue() {
            return this.values.get(this.getIndex());
        }

        @Override
        public void setValue(int value) {
            int index = this.nearestIndex(value);
            if (this.values.size() > 1) {
                this.setProportion((double) index / (this.values.size() - 1));
            }
        }

        private void step(int step) {
            int index = Math.clamp(this.getIndex() + step, 0, this.values.size() - 1);
            this.setValueWithUpdate(this.values.get(index));
        }
    }
}
