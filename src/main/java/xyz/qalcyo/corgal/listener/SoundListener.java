package xyz.qalcyo.corgal.listener;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.corgal.utils.HypixelUtils;
import xyz.qalcyo.requisite.Requisite;

public class SoundListener {
    private static boolean shouldPlay = true;
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            if (Requisite.getInstance().getHypixelHelper().isOnHypixel() && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null) {
                if (CorgalConfig.blockNotify && !HypixelUtils.isLobby()) {
                    if (Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().stackSize <= CorgalConfig.blockNumber && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().stackSize > 0) {
                        if (CorgalConfig.spamBlockNotify) {
                            playSound();
                        } else {
                            if (shouldPlay) {
                                playSound();
                                shouldPlay = false;
                            }
                        }
                    }
                }
            }
        }
    }

    public void playSound() {
        if (!Minecraft.getMinecraft().playerController.gameIsSurvivalOrAdventure()) return;
        switch (CorgalConfig.blockNotifySound) {
            case 0: {
                Minecraft.getMinecraft().thePlayer.playSound("random.orb", 1f, 1f);
            }
            case 1: {
                Minecraft.getMinecraft().thePlayer.playSound("mob.irongolem.hit", 1f, 1f);
            }
            case 2: {
                Minecraft.getMinecraft().thePlayer.playSound("mob.blaze.hit", 1f, 1f);
            }
            case 3: {
                Minecraft.getMinecraft().thePlayer.playSound("random.anvil_land", 1f, 1f);
            }
            case 4: {
                Minecraft.getMinecraft().thePlayer.playSound("mob.horse.death", 1f, 1f);
            }
            case 5: {
                Minecraft.getMinecraft().thePlayer.playSound("mob.ghast.scream", 1f, 1f);
            }
            case 6: {
                Minecraft.getMinecraft().thePlayer.playSound("mob.guardian.land.hit", 1f, 1f);
            }
            case 7: {
                Minecraft.getMinecraft().thePlayer.playSound("mob.cat.meow", 1f, 1f);
            }
            case 8: {
                Minecraft.getMinecraft().thePlayer.playSound("mob.wolf.bark", 1f, 1f);
            }
        }
    }
}
