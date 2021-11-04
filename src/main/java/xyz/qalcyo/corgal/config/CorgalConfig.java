package xyz.qalcyo.corgal.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import net.minecraft.client.Minecraft;
import xyz.qalcyo.corgal.Corgal;

import java.io.File;

//Vigilance will be removed in the future
public class CorgalConfig extends Vigilant {

    @Property(
            type = PropertyType.TEXT,
            name = "API Key",
            description = "The API Key, for some features that require accessing to the Hypixel API such as the Auto GEXP and winstreak features.",
            category = "API"
    )
    public static String apiKey = "";

    @Property(
            type = PropertyType.SWITCH,
            name = "Automatically Get API Key",
            description = "Automatically get the API Key from /api new.",
            category = "General"
    )
    public static boolean autoGetAPI = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Automatically Check GEXP",
            description = "Automatically check your GEXP after you win a Hypixel game. \\u00a7cRequires an API Key.",
            category = "Automatic"
    )
    public static boolean autoGetGEXP = false;

    @Property(
            type = PropertyType.SELECTOR,
            name = "GEXP Mode",
            description = "Choose which GEXP to get.",
            category = "Automatic",
            options = {"Daily", "Weekly"}
    )
    public static int gexpMode = 0;

    @Property(
            type = PropertyType.SWITCH,
            name = "Automatically Check Winstreak",
            description = "Automatically check your winstreak after you win a Hypixel game. \\u00a7cRequires an API Key.",
            category = "Automatic"
    )
    public static boolean autoGetWinstreak = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Height Overlay",
            description = "Make blocks that are in the Hypixel height limit a different colour.\nReloads chunks automatically when toggled on and off.",
            category = "Overlay"
    )
    public static boolean heightOverlay = false;

    @Property(
            type = PropertyType.DECIMAL_SLIDER,
            name = "Overlay Tint Multiplier",
            description = "Adjust the tint multiplier.",
            category = "Overlay",
            maxF = 1.0F
    )
    public static float overlayAmount = 0.7F;

    @Property(
            type = PropertyType.SWITCH,
            name = "Notify When Blocks Run Out",
            description = "Pings you via a sound when your blocks are running out.",
            category = "Blocks"
    )
    public static boolean blockNotify = false;

    @Property(
            type = PropertyType.NUMBER,
            name = "Block Number",
            description = "Modify the number of blocks you (don't?) have for the Notify When Blocks Run Out feature to work.",
            category = "Blocks"
    )
    public static int blockNumber = 10;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Sound",
            description = "Choose what sound to play.",
            category = "Blocks",
            options = {"Hypixel Ding", "Golem Hit", "Blaze Hit", "Anvil Land", "Horse Death", "Ghast Scream", "Guardian Floop", "Cat Meow", "Dog Bark"}
    )
    public static int blockNotifySound = 0;

    @Property(
            type = PropertyType.SWITCH,
            name = "Spam Sound",
            description = "Spam the sound (this will make it VERY loud)",
            category = "Blocks"
    )
    public static boolean spamBlockNotify = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Duels Cosmetics",
            description = "Hide Duels Cosmetics in Hypixel.",
            category = "Cosmetics"
    )
    public static boolean hideDuelsCosmetics = false;

    public static CorgalConfig instance = new CorgalConfig();

    public CorgalConfig() {
        super(new File(Corgal.modDir, "corgal.toml"), Corgal.NAME);
        initialize();
        registerListener("heightOverlay", (funny) -> {
            if (funny != null) {
                heightOverlay = (boolean) funny;
                Minecraft.getMinecraft().renderGlobal.loadRenderers();
            }
        });
        registerListener("overlayAmount", (funny) -> {
            if (funny != null) {
                overlayAmount = (float) funny;
                Minecraft.getMinecraft().renderGlobal.loadRenderers();
            }
        });
    }
}
