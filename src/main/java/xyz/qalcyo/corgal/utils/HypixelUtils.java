package xyz.qalcyo.corgal.utils;

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
import xyz.qalcyo.requisite.Requisite;
import xyz.qalcyo.requisite.core.integration.hypixel.events.LocrawReceivedEvent;
import xyz.qalcyo.requisite.core.integration.hypixel.locraw.HypixelLocraw;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class HypixelUtils {
    public static String gexp;
    public static String winstreak;
    private static HypixelLocraw locraw;

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

    public static boolean isLobby() {
        if (Requisite.getInstance().getHypixelHelper().isOnHypixel()) {
            if (locraw != null) {
                return locraw.getGameMode() == null || !StringsKt.isBlank(locraw.getGameMode()) || locraw.getGameType() == null;
            }
        }
        return false;
    }

    private static String getCurrentESTTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

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
                default: return false;
            }
        }
        return true;
    }

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
                default: return false;
            }
        }
        return true;
    }

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
                default: return false;
            }
        }
        return true;
    }

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
    }
}
