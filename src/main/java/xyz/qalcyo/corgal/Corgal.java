package xyz.qalcyo.corgal;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import xyz.qalcyo.corgal.commands.CorgalCommand;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.corgal.listener.ChatListener;
import xyz.qalcyo.corgal.listener.SoundListener;
import xyz.qalcyo.corgal.utils.HypixelUtils;
import xyz.qalcyo.requisite.Requisite;

import java.io.File;

@Mod(name = Corgal.NAME, version = Corgal.VER, modid = Corgal.ID)
public class Corgal {

    public static final String
            NAME = "@NAME@",
            VER = "@VER@",
            ID = "@ID@";

    public static File modDir = new File(new File(new File(Minecraft.getMinecraft().mcDataDir, "config"), "Qalcyo"), NAME);

    /**
     * Sends a client-side only message to the player with the mod's name as a prefix.
     *
     * @param message The message to send.
     */
    public static void sendMessage(String message) {
        Requisite.getInstance().getChatHelper().send(EnumChatFormatting.BLUE + "[" + Corgal.NAME + "]", message);
    }

    @Mod.EventHandler
    private void onInitializationEvent(FMLInitializationEvent event) {
        if (!modDir.exists()) modDir.mkdirs();
        CorgalConfig.instance.preload();
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new SoundListener());
        Requisite.getInstance().getEventBus().register(new HypixelUtils());
        ClientCommandHandler.instance.registerCommand(new CorgalCommand());
    }

}
