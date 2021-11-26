package xyz.qalcyo.corgal.mixin;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.corgal.utils.HypixelUtils;
import xyz.qalcyo.requisite.core.integration.hypixel.locraw.HypixelLocraw;

@Mixin(World.class)
public class WorldMixin {

    @Inject(method = "spawnParticle(IZDDDDDD[I)V", at = @At("HEAD"), cancellable = true)
    private void removeParticles(CallbackInfo ci, int particleID, boolean p_175720_2_, double xCood, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175720_15_) {
        if (HypixelUtils.locraw != null && HypixelUtils.locraw.getGameType() == HypixelLocraw.GameType.DUELS && !HypixelUtils.isLobby()) {
            switch (EnumParticleTypes.getParticleFromId(particleID)) {
                case WATER_SPLASH:
                case SPELL:
                case SPELL_MOB:
                case SPELL_MOB_AMBIENT:
                case SPELL_WITCH:
                case NOTE:
                case LAVA:
                case HEART:
                case REDSTONE:
                case SLIME:
                    ci.cancel();
            }
        }
    }
}
