package xyz.qalcyo.corgal.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
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
    public static boolean autoGetGEXP = true;

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

    public static CorgalConfig instance = new CorgalConfig();
    public CorgalConfig() {
        super(new File(Corgal.modDir, "corgal.toml"), Corgal.NAME);
        initialize();
    }
}
