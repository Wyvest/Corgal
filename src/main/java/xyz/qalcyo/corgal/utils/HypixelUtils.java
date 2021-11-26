package xyz.qalcyo.corgal.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kotlin.text.StringsKt;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import xyz.qalcyo.corgal.Corgal;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.eventbus.SubscribeEvent;
import xyz.qalcyo.mango.Multithreading;
import xyz.qalcyo.requisite.Requisite;
import xyz.qalcyo.requisite.core.integration.hypixel.events.LocrawReceivedEvent;
import xyz.qalcyo.requisite.core.integration.hypixel.locraw.HypixelLocraw;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HypixelUtils {
    private static final File limitFile = new File(Corgal.modDir, "limits.json");
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final Cache<String, Integer> limitCache = Caffeine.newBuilder().executor(new ThreadPoolExecutor(50, 50,
            0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            r -> new Thread(r, String.format("Corgal Cache Thread %s", counter.incrementAndGet())))).maximumSize(5000).build();
    public static String gexp;
    public static String winstreak;
    public static HypixelLocraw locraw;
    private static Future downloadTask = null;
    private static JsonObject limitJson = null;
    public static int height = -1;

    public static void getHeight() {
        if (locraw == null || downloadTask == null || !downloadTask.isDone() || !limitFile.exists() || limitJson == null || isLobby()) {
            height = -1;
            return;
        }
        try {
            switch (Objects.requireNonNull(locraw).getGameType()) {
                case BEDWARS:
                    if (locraw.getMapName() != null && !StringsKt.isBlank(locraw.getMapName())) {
                        String map = locraw.getMapName().toLowerCase(Locale.ENGLISH).replace(" ", "_");
                        Integer cached = limitCache.getIfPresent(map);
                        if (cached == null) {
                            limitCache.put(map, (limitJson.getAsJsonObject("bedwars").get(map).getAsInt()));
                            Integer funny = limitCache.getIfPresent(map);
                            if (funny == null) {
                                height = -1;
                                return;
                            } else {
                                height = funny;
                            }
                        } else {
                            height = cached;
                        }
                    } else {
                        height = -1;
                    }
                    return;
                case DUELS:
                    if (locraw.getGameMode().toLowerCase(Locale.ENGLISH).contains("bridge") || locraw.getGameMode().toLowerCase(Locale.ENGLISH).contains("ctf")) {
                        height = 100;
                        return;
                    }
                default:
                    height = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            height = -1;
        }
    }

    /**
     * @return Whether the player is in Hypixel Skyblock.
     */
    public static boolean isSkyblock() {
        if (Requisite.getInstance().getHypixelHelper().isOnHypixel()) {
            ScoreObjective scoreboardObj = Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
            if (scoreboardObj != null) {
                String scObjName = cleanSB(scoreboardObj.getDisplayName());
                return scObjName.contains("SKYBLOCK");
            }
        }
        return false;
    }

    /**
     * @return Whether the player is in a Hypixel lobby.
     */
    public static boolean isLobby() {
        if (Requisite.getInstance().getHypixelHelper().isOnHypixel()) {
            if (locraw != null) {
                return locraw.getGameMode() == null || StringsKt.isBlank(locraw.getGameMode()) || locraw.getGameType() == null;
            }
        }
        return false;
    }

    private static String getCurrentESTTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    /**
     * Gets the player's GEXP and stores it in a variable.
     *
     * @return Whether the "getting" was successful.
     */
    public static boolean getGEXP() {
        String gexp = null;
        String uuid = Minecraft.getMinecraft().thePlayer.getGameProfile().getId().toString().replace("-", "");
        JsonArray guildMembers = APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + CorgalConfig.apiKey + ";player=" + uuid).getAsJsonObject("guild").getAsJsonArray("members");
        for (JsonElement e : guildMembers) {
            if (e.getAsJsonObject().get("uuid").getAsString().equals(uuid)) {
                gexp = Integer.toString(e.getAsJsonObject().getAsJsonObject("expHistory").get(getCurrentESTTime()).getAsInt());
                break;
            }
        }
        if (gexp == null) return false;
        HypixelUtils.gexp = gexp;
        return true;
    }

    /**
     * Gets the specified player's GEXP and stores it in a variable.
     *
     * @param username The username of the player.
     * @return Whether the "getting" was successful.
     */
    public static boolean getGEXP(String username) {
        String gexp = null;
        String uuid = getUUID(username);
        JsonArray guildMembers = APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + CorgalConfig.apiKey + ";player=" + uuid).getAsJsonObject("guild").getAsJsonArray("members");
        for (JsonElement e : guildMembers) {
            if (e.getAsJsonObject().get("uuid").getAsString().equals(uuid)) {
                gexp = Integer.toString(e.getAsJsonObject().getAsJsonObject("expHistory").get(getCurrentESTTime()).getAsInt());
                break;
            }
        }
        if (gexp == null) return false;
        HypixelUtils.gexp = gexp;
        return true;
    }

    /**
     * Gets the player's weekly GEXP and stores it in a variable.
     *
     * @return Whether the "getting" was successful.
     */
    public static boolean getWeeklyGEXP() {
        String gexp = null;
        String uuid = Minecraft.getMinecraft().thePlayer.getGameProfile().getId().toString().replace("-", "");
        JsonArray guildMembers = APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + CorgalConfig.apiKey + ";player=" + uuid).getAsJsonObject("guild").getAsJsonArray("members");
        for (JsonElement e : guildMembers) {
            if (e.getAsJsonObject().get("uuid").getAsString().equals(uuid)) {
                int addGEXP = 0;
                for (Map.Entry<String, JsonElement> set : e.getAsJsonObject().get("expHistory").getAsJsonObject().entrySet()) {
                    addGEXP += set.getValue().getAsInt();
                }
                gexp = Integer.toString(addGEXP);
                break;
            }
        }
        if (gexp == null) return false;
        HypixelUtils.gexp = gexp;
        return true;
    }

    /**
     * Gets the player's weekly GEXP and stores it in a variable.
     *
     * @param username The username of the player.
     * @return Whether the "getting" was successful.
     */
    public static boolean getWeeklyGEXP(String username) {
        String gexp = null;
        String uuid = getUUID(username);
        JsonArray guildMembers = APIUtil.getJSONResponse("https://api.hypixel.net/guild?key=" + CorgalConfig.apiKey + ";player=" + uuid).getAsJsonObject("guild").getAsJsonArray("members");
        for (JsonElement e : guildMembers) {
            if (e.getAsJsonObject().get("uuid").getAsString().equals(uuid)) {
                int addGEXP = 0;
                for (Map.Entry<String, JsonElement> set : e.getAsJsonObject().get("expHistory").getAsJsonObject().entrySet()) {
                    addGEXP += set.getValue().getAsInt();
                }
                gexp = Integer.toString(addGEXP);
                break;
            }
        }
        if (gexp == null) return false;
        HypixelUtils.gexp = gexp;
        return true;
    }

    /**
     * Gets the player's current winstreak and stores it in a variable.
     *
     * @return Whether the "getting" was successful.
     */
    public static boolean getWinstreak() {
        String uuid = Minecraft.getMinecraft().thePlayer.getGameProfile().getId().toString().replace("-", "");
        JsonObject playerStats =
                APIUtil.getJSONResponse("https://api.hypixel.net/player?key=" + CorgalConfig.apiKey + ";uuid=" + uuid).getAsJsonObject("player").getAsJsonObject("stats");
        if (locraw != null) {
            switch (locraw.getGameType()) {
                case BEDWARS: {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("Bedwars").get("winstreak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                case SKYWARS: {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("SkyWars").get("win_streak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                case DUELS: {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("Duels").get("current_winstreak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                default:
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets the specified player's current winstreak and stores it in a variable.
     *
     * @param username The username of the player.
     * @return Whether the "getting" was successful.
     */
    public static boolean getWinstreak(String username) {
        String uuid = getUUID(username);
        JsonObject playerStats =
                APIUtil.getJSONResponse("https://api.hypixel.net/player?key=" + CorgalConfig.apiKey + ";uuid=" + uuid).getAsJsonObject("player").getAsJsonObject("stats");
        if (locraw != null) {
            switch (locraw.getGameType()) {
                case BEDWARS: {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("Bedwars").get("winstreak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                case SKYWARS: {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("SkyWars").get("win_streak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                case DUELS: {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("Duels").get("current_winstreak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                default:
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets the player's current winstreak and stores it in a variable.
     *
     * @param username The username of the player.
     * @param game     The game to get the stats.
     * @return Whether the "getting" was successful.
     */
    public static boolean getWinstreak(String username, String game) {
        String uuid = getUUID(username);
        JsonObject playerStats =
                APIUtil.getJSONResponse("https://api.hypixel.net/player?key=" + CorgalConfig.apiKey + ";uuid=" + uuid).getAsJsonObject("player").getAsJsonObject("stats");
        if (game != null) {
            switch (game.toLowerCase(Locale.ENGLISH)) {
                case "bedwars": {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("Bedwars").get("winstreak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                case "skywars": {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("SkyWars").get("win_streak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                case "duels": {
                    try {
                        winstreak = Integer.toString(playerStats.getAsJsonObject("Duels").get("current_winstreak").getAsInt());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                default:
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets a UUID based on the username provided.
     *
     * @param username The username of the player to get.
     */
    public static String getUUID(String username) {
        JsonObject uuidResponse =
                APIUtil.getJSONResponse("https://api.mojang.com/users/profiles/minecraft/" + username);
        if (uuidResponse.has("error")) {
            Corgal.sendMessage(
                    EnumChatFormatting.RED + "Failed with error: " + uuidResponse.get("reason").getAsString()
            );
            return null;
        }
        return uuidResponse.get("id").getAsString();
    }

    private static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.stripControlCodes(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();

        for (char c : nvString) {
            if ((int) c > 20 && (int) c < 127) {
                cleaned.append(c);
            }
        }

        return cleaned.toString();
    }

    @SubscribeEvent
    public void onLocraw(LocrawReceivedEvent event) {
        locraw = event.locraw;
        if (downloadTask == null) {
            downloadTask = Multithreading.submit(() -> {
                if (APIUtil.download("https://api.pinkulu.com/HeightLimitMod/Limits", limitFile)) {
                    limitJson = Objects.requireNonNull(JsonUtils.read("limits.json", limitFile.getParentFile())).getAsJsonObject();
                } else {
                    System.out.println("Downloading has failed, trying again on next locraw sent.");
                    downloadTask = null;
                }
            });
            return;
        }
        getHeight();
    }
}
