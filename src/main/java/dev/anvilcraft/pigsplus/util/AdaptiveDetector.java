package dev.anvilcraft.pigsplus.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 缓存检测结果，并在结果保持不变时逐渐延长检测间隔。
 */
public final class AdaptiveDetector<T> {
    private final int base;
    private final int max;
    private final int checks;
    private T value;
    private int interval;
    private int cooldown;
    private int same;

    public AdaptiveDetector(T value, int base, int max, int checks) {
        if (base < 1) throw new IllegalArgumentException("base must be positive");
        if (max < base) throw new IllegalArgumentException("max must not be less than base");
        if (checks < 1) throw new IllegalArgumentException("checks must be positive");
        this.base = base;
        this.max = max;
        this.checks = checks;
        this.set(value);
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    /**
     * 推进计时器，并在到达检测时间时执行检测。
     *
     * @return 缓存的检测结果是否发生变化
     */
    public boolean tick(Supplier<? extends T> detector) {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        T next = detector.get();
        boolean changed = !Objects.equals(this.value, next);
        if (changed) {
            this.value = next;
            this.interval = this.base;
            this.same = 0;
        } else if (++this.same >= this.checks) {
            this.interval = Math.min(this.interval + this.base, this.max);
            this.same = 0;
        }
        this.cooldown = this.interval - 1;
        return changed;
    }
}
