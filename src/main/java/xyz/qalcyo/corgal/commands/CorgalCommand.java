package xyz.qalcyo.corgal.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import xyz.qalcyo.corgal.Corgal;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.corgal.utils.HypixelUtils;
import xyz.qalcyo.mango.Multithreading;
import xyz.qalcyo.requisite.Requisite;

import java.util.Locale;

public class CorgalCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return Corgal.ID;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            Requisite.getInstance().getGuiHelper().open(CorgalConfig.instance.gui());
        } else {
            switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "gexp": {
                    Multithreading.runAsync(() -> {
                        if (CorgalConfig.apiKey.isEmpty() || !Requisite.getInstance().getHypixelHelper().getApi().isValidKey(CorgalConfig.apiKey)) {
                            Corgal.sendMessage(EnumChatFormatting.RED + "You need to provide a valid API key to run this command! Type /api new to autoset a key.");
                            return;
                        }
                        switch (args.length) {
                            case 3: {
                                switch (args[2]) {
                                    case "daily": {
                                        if (HypixelUtils.getGEXP(args[1])) {
                                            Requisite.getInstance().getNotifications()
                                                    .push(Corgal.NAME, args[1] + " currently has " + HypixelUtils.gexp + " daily guild EXP.");
                                        } else {
                                            Requisite.getInstance().getNotifications()
                                                    .push(Corgal.NAME, "There was a problem trying to get " + args[1] + "'s daily GEXP.");
                                        }
                                        return;
                                    }
                                    case "weekly": {
                                        if (HypixelUtils.getWeeklyGEXP(args[1])) {
                                            Requisite.getInstance().getNotifications()
                                                    .push(Corgal.NAME, args[1] + " currently has " + HypixelUtils.gexp + " weekly guild EXP.");
                                        } else {
                                            Requisite.getInstance().getNotifications()
                                                    .push(Corgal.NAME, "There was a problem trying to get " + args[1] + "'s weekly GEXP.");
                                        }
                                        return;
                                    }
                                    default: {
                                        Corgal.sendMessage("Invalid command usage.");
                                        return;
                                    }
                                }
                            }
                            case 2: {
                                if (HypixelUtils.getGEXP(args[1])) {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, args[1] + " currently has " + HypixelUtils.gexp + " guild EXP.");
                                } else {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, "There was a problem trying to get " + args[1] + "'s GEXP.");
                                }
                                return;
                            }
                            default: {
                                if (HypixelUtils.getGEXP()) {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, "You currently have " + HypixelUtils.gexp + " guild EXP.");
                                } else {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, "There was a problem trying to get your GEXP.");
                                }
                            }
                        }
                    });
                    return;
                }
                case "winstreak": {
                    Multithreading.runAsync(() -> {
                        if (CorgalConfig.apiKey.isEmpty() || !Requisite.getInstance().getHypixelHelper().getApi().isValidKey(CorgalConfig.apiKey)) {
                            Corgal.sendMessage(EnumChatFormatting.RED + "You need to provide a valid API key to run this command! Type /api new to autoset a key.");
                            return;
                        }
                        switch (args.length) {
                            case 3: {
                                if (HypixelUtils.getWinstreak(args[1], args[2])) {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, args[1] + " currently has a " + HypixelUtils.winstreak + " winstreak in " + args[2] + ".");
                                } else {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, "There was a problem trying to get " + args[1] + "'s winstreak in " + args[2] + ".");
                                }
                                return;
                            }
                            case 2: {
                                if (HypixelUtils.getWinstreak(args[1])) {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, args[1] + " currently has a " + HypixelUtils.winstreak + " winstreak.");
                                } else {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, "There was a problem trying to get " + args[1] + "'s winstreak.");
                                }
                                return;
                            }
                            default: {
                                if (HypixelUtils.getWinstreak()) {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, "You currently have a " + HypixelUtils.winstreak + " winstreak.");
                                } else {
                                    Requisite.getInstance().getNotifications()
                                            .push(Corgal.NAME, "There was a problem trying to get your winstreak.");
                                }
                            }
                        }
                    });
                    return;
                }
                case "setkey": {
                    Multithreading.runAsync(() -> {
                        if (args.length >= 3 && Requisite.getInstance().getHypixelHelper().getApi().isValidKey(args[2])) {
                            CorgalConfig.apiKey = args[2];
                            CorgalConfig.instance.markDirty();
                            CorgalConfig.instance.writeData();
                            Corgal.sendMessage(EnumChatFormatting.GREEN + "Saved API key successfully!");
                        } else {
                            Corgal.sendMessage(EnumChatFormatting.RED + "Invalid API key! Please try again.");
                        }
                    });
                    return;
                }
                default: {
                    Corgal.sendMessage("Invalid command usage.");
                }
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
