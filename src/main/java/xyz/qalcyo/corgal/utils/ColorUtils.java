package xyz.qalcyo.corgal.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.minecraft.block.material.MapColor;
import xyz.qalcyo.corgal.Corgal;
import xyz.qalcyo.corgal.config.CorgalConfig;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorUtils {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
        50, 50,
                0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), (r) -> new Thread(
                r,
                String.format("%s Cache Thread %s", Corgal.NAME, counter.incrementAndGet())
        )
    );

    private static final Cache<Integer, Integer> cache = Caffeine.newBuilder().executor(POOL).maximumSize(100).build();

    public static int getCachedDarkColor(MapColor mapColor) {
        Integer color = cache.getIfPresent(mapColor.colorIndex);
        if (color == null) {
            cache.put(mapColor.colorIndex, ((0xFF) << 24) |
                    ((Math.round(Math.max((float) ColorUtils.getRed(mapColor.colorValue) * CorgalConfig.overlayAmount, 0.0F)) & 0xFF) << 16) |
                    ((Math.round(Math.max((float) ColorUtils.getGreen(mapColor.colorValue) * CorgalConfig.overlayAmount, 0.0F)) & 0xFF) << 8) |
                    ((Math.round(Math.max((float) ColorUtils.getBlue(mapColor.colorValue) * CorgalConfig.overlayAmount, 0.0F)) & 0xFF)));
            return Objects.requireNonNull(cache.getIfPresent(mapColor.colorIndex));
        } else {
            return color;
        }
    }

    /**
     * @return The red value of the provided RGBA value.
     */
    public static int getRed(int rgba) {
        return (rgba >> 16) & 0xFF;
    }

    /**
     * @return The green value of the provided RGBA value.
     */
    public static int getGreen(int rgba) {
        return (rgba >> 8) & 0xFF;
    }

    /**
     * @return The blue value of the provided RGBA value.
     */
    public static int getBlue(int rgba) {
        return (rgba) & 0xFF;
    }
}
