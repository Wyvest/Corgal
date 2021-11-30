package xyz.qalcyo.corgal.listener;

import gg.essential.universal.wrappers.message.UTextComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.qalcyo.corgal.Corgal;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.corgal.mixin.GuiIngameAccessor;
import xyz.qalcyo.corgal.utils.HypixelUtils;
import xyz.qalcyo.corgal.utils.PatternHandler;
import xyz.qalcyo.mango.Multithreading;
import xyz.qalcyo.requisite.Requisite;
import xyz.qalcyo.requisite.core.integration.hypixel.locraw.HypixelLocraw;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The listener for chat messages.
 */
public class ChatListener {

    private final int apiKeyMessageLength = "Your new API key is ".length();
    private boolean victoryDetected = false;
    private boolean glDetected = false;
    private static final String[] cancelGlMessages = {"glhf", "Good Luck", "GL", "Have a good game!", "gl", "Good luck!", "AutoGL By Sk1er"};
    private static final String[] glmessages = {"glhf", "Good Luck", "GL", "Have a good game!", "gl", "Good luck!"};

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.type == 2) return; // cancels action bar messages from appearing
        if (Requisite.getInstance().getHypixelHelper().isOnHypixel()) { // checks whether the player is on hypixel
            String unformattedText = UTextComponent.Companion.stripFormatting(event.message.getUnformattedText()); // gets the message without useless codes
            if (CorgalConfig.autoGetAPI) {
                if (unformattedText.startsWith("Your new API key is ")) { // if the message starts with this, then get the API key from the message
                    String tempApiKey = unformattedText.substring(apiKeyMessageLength);
                    Multithreading.runAsync(() -> { //run this async as getting from the API normally would freeze minecraft
                        if (!Requisite.getInstance().getHypixelHelper().getApi().isValidKey(tempApiKey)
                        ) {
                            if (!Requisite.getInstance().getHypixelHelper().isOnHypixel()) {
                                Corgal.sendMessage(EnumChatFormatting.RED + "You are not running this command on Hypixel! This mod needs an Hypixel API key!");
                            } else {
                                Corgal.sendMessage(EnumChatFormatting.RED + "The API Key was invalid! Please try running the command again.");
                            }
                        } else {
                            // if the api key is valid add the key to the configuration and save it
                            CorgalConfig.apiKey = tempApiKey;
                            CorgalConfig.instance.markDirty();
                            CorgalConfig.instance.writeData();
                            Corgal.sendMessage(EnumChatFormatting.GREEN + "Your API Key has been automatically configured.");
                        }
                    });
                }
            }
            if (CorgalConfig.antiGL) {
                for (String glMessage : cancelGlMessages) {
                    if (unformattedText.contains(glMessage)) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
            if (CorgalConfig.autoGL && !glDetected) {
                if (unformattedText.startsWith("The game starts in 5 seconds!")) {
                    glDetected = true;
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/ac " + getGLMessage());
                    return;
                }
            }
            if (CorgalConfig.guildWelcome) {
                Multithreading.runAsync(() -> {
                    Pattern cached = PatternHandler.cache.getIfPresent("guildPlayerJoinPattern");
                    if (cached != null) {
                        Matcher matcher = cached.matcher(unformattedText);
                        if (matcher.matches()) {
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("/gc Welcome to the guild, " + matcher.group("player") + "!");
                        }
                    }
                });
            }
            if ((CorgalConfig.autoGetGEXP || CorgalConfig.autoGetWinstreak) && PatternHandler.gameEnd.size() != 0) {
                if (!victoryDetected) { // prevent victories being detected twice
                    Multithreading.runAsync(() -> { //run this async as getting from the API normally would freeze minecraft
                        for (Pattern triggers : PatternHandler.gameEnd) {
                            if (triggers.matcher(unformattedText).matches()) {
                                victoryDetected = true;
                                if (CorgalConfig.autoGetGEXP) {
                                    if (CorgalConfig.gexpMode == 0) {
                                        if (HypixelUtils.getGEXP()) {
                                            Requisite.getInstance().getNotifications()
                                                    .push(
                                                            Corgal.NAME,
                                                            "You currently have " + HypixelUtils.gexp + " daily guild EXP."
                                                    );
                                        } else {
                                            Requisite.getInstance().getNotifications()
                                                    .push(Corgal.NAME, "There was a problem trying to get your GEXP.");
                                        }
                                    } else {
                                        if (HypixelUtils.getWeeklyGEXP()) {
                                            Requisite.getInstance().getNotifications()
                                                    .push(
                                                            Corgal.NAME,
                                                            "You currently have " + HypixelUtils.gexp + " weekly guild EXP."
                                                    );
                                        } else {
                                            Requisite.getInstance().getNotifications()
                                                    .push(Corgal.NAME, "There was a problem trying to get your GEXP.");
                                        }
                                    }
                                }
                                if (isSupportedMode(HypixelUtils.locraw) && CorgalConfig.autoGetWinstreak) {
                                    if (HypixelUtils.getWinstreak()) {
                                        Requisite.getInstance().getNotifications().push(
                                                Corgal.NAME,
                                                "You currently have a " + HypixelUtils.winstreak + " winstreak."
                                        );
                                    } else {
                                        Requisite.getInstance().getNotifications()
                                                .push(Corgal.NAME, "There was a problem trying to get your winstreak.");
                                    }
                                }
                                return;
                            }
                        }
                        String title = ((GuiIngameAccessor) Minecraft.getMinecraft().ingameGUI).getDisplayedTitle().toLowerCase(Locale.ENGLISH);
                        if (title.equals("victory!") || title.equals("game over") || title.equals("game over!") || title.endsWith(" wins") || title.endsWith(" wins!")) {
                            victoryDetected = true;
                            if (CorgalConfig.autoGetGEXP) {
                                if (CorgalConfig.gexpMode == 0) {
                                    if (HypixelUtils.getGEXP()) {
                                        Requisite.getInstance().getNotifications()
                                                .push(
                                                        Corgal.NAME,
                                                        "You currently have " + HypixelUtils.gexp + " daily guild EXP."
                                                );
                                    } else {
                                        Requisite.getInstance().getNotifications()
                                                .push(Corgal.NAME, "There was a problem trying to get your GEXP.");
                                    }
                                } else {
                                    if (HypixelUtils.getWeeklyGEXP()) {
                                        Requisite.getInstance().getNotifications()
                                                .push(
                                                        Corgal.NAME,
                                                        "You currently have " + HypixelUtils.gexp + " weekly guild EXP."
                                                );
                                    } else {
                                        Requisite.getInstance().getNotifications()
                                                .push(Corgal.NAME, "There was a problem trying to get your GEXP.");
                                    }
                                }
                            }
                            if (isSupportedMode(HypixelUtils.locraw) && CorgalConfig.autoGetWinstreak) {
                                if (HypixelUtils.getWinstreak()) {
                                    Requisite.getInstance().getNotifications().push(
                                            Corgal.NAME,
                                            "You currently have a " + HypixelUtils.winstreak + " winstreak."
                                    );
                                } else {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, "There was a problem trying to get your winstreak.");
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldLeave(WorldEvent.Unload event) {
        victoryDetected = false;
        glDetected = false;
    }

    private static String getGLMessage() {
        return glmessages[CorgalConfig.glPhrase];
    }

    private boolean isSupportedMode(HypixelLocraw locraw) {
        if (locraw != null && locraw.getGameType() != null) {
            switch (locraw.getGameType()) {
                case BEDWARS:
                case SKYWARS:
                case DUELS:
                    return true;
            }
        }
        return false;
    }
}
