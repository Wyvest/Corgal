package xyz.qalcyo.corgal.listener;

import gg.essential.universal.wrappers.message.UTextComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.qalcyo.corgal.Corgal;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.corgal.utils.HypixelUtils;
import xyz.qalcyo.mango.Multithreading;
import xyz.qalcyo.requisite.Requisite;
import xyz.qalcyo.requisite.core.integration.hypixel.locraw.HypixelLocraw;

import java.util.Arrays;
import java.util.List;

/**
 * The listener for chat messages.
 */
public class ChatListener {

    private final int apiKeyMessageLength = "Your new API key is ".length();
    private final List<String> gameEndList = Arrays.asList(
            "Winner #1 (",
            "Top Survivors",
            "Winners - ",
            "Winners: ",
            "Winner: ",
            "Winning Team: ",
            " won the game!",
            "Top Seeker: ",
            "Last team standing!",
            "1st Place: ",
            "1st Killer - ",
            "1st Place - ",
            "Winner: ",
            " - Damage Dealt - ",
            "Winning Team -",
            "1st - ",
            " Duel - ",
            "YOU LOSE!"
    );
    private boolean victoryDetected = false;

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.type == 2) return;
        if (Requisite.getInstance().getHypixelHelper().isOnHypixel()) {
            String unformattedText = UTextComponent.Companion.stripFormatting(event.message.getUnformattedText());
            if (CorgalConfig.autoGetAPI) {
                if (unformattedText.startsWith("Your new API key is ")) {
                    String tempApiKey = unformattedText.substring(apiKeyMessageLength);
                    Multithreading.runAsync(() -> {
                        if (!Requisite.getInstance().getHypixelHelper().getApi().isValidKey(tempApiKey)
                        ) {
                            if (!Requisite.getInstance().getHypixelHelper().isOnHypixel()) {
                                Corgal.sendMessage(EnumChatFormatting.RED + "You are not running this command on Hypixel! This mod needs an Hypixel API key!");
                            } else {
                                Corgal.sendMessage(EnumChatFormatting.RED + "The API Key was invalid! Please try running the command again.");
                            }
                        } else {
                            CorgalConfig.apiKey = tempApiKey;
                            CorgalConfig.instance.markDirty();
                            CorgalConfig.instance.writeData();
                            Corgal.sendMessage(EnumChatFormatting.GREEN + "Your API Key has been automatically configured.");
                        }
                    });
                }
            }
            if (CorgalConfig.autoGetGEXP || CorgalConfig.autoGetWinstreak) {
                if (!victoryDetected) {
                    Multithreading.runAsync(() -> {
                        if (unformattedText.startsWith(" ")) {
                            for (String triggers : gameEndList) {
                                if (unformattedText.contains(triggers)) {
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
                                    break;
                                }
                            }
                        }
                    });
                }
            }
        }
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
