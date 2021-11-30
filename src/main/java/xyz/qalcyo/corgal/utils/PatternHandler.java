package xyz.qalcyo.corgal.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kotlin.collections.CollectionsKt;
import xyz.qalcyo.corgal.Corgal;
import xyz.qalcyo.mango.Multithreading;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class PatternHandler {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            50, 50,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), (r) -> new Thread(
            r,
            String.format("%s Cache Thread %s", Corgal.NAME, counter.incrementAndGet())
    )
    );

    public static final Cache<String, Pattern> cache = Caffeine.newBuilder().executor(POOL).maximumSize(100).build();
    public static JsonObject regexJson = null;
    public static ArrayList<Pattern> gameEnd = CollectionsKt.arrayListOf();

    public static void initialize() {
        Multithreading.runAsync(() -> {
            regexJson = APIUtil.getJSONResponse("https://raw.githubusercontent.com/Qalcyo/DataStorage/master/corgal/regex.json");
            for (JsonElement element : regexJson.getAsJsonArray("game_end")) {
                gameEnd.add(Pattern.compile(element.getAsString()));
            }
            for (JsonElement element : regexJson.getAsJsonArray("misc")) {
                cache.put(element.getAsJsonObject().get("id").getAsString(), Pattern.compile(element.getAsJsonObject().get("regex").getAsString()));
            }
        });
    }

}
