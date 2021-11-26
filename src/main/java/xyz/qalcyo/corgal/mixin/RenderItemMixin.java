package xyz.qalcyo.corgal.mixin;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.corgal.utils.HypixelUtils;
import xyz.qalcyo.requisite.core.integration.hypixel.locraw.HypixelLocraw;

@Mixin(RenderItem.class)
public class RenderItemMixin {
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resources/model/IBakedModel;)V", at = @At("HEAD"), cancellable = true)
    private void yeah(ItemStack stack, IBakedModel model, CallbackInfo ci) {
        if (stack == null) return;
        if (CorgalConfig.hideDuelsCosmetics && HypixelUtils.locraw != null && HypixelUtils.locraw.getGameType() == HypixelLocraw.GameType.DUELS && !HypixelUtils.isLobby() && (stack.getItem() instanceof ItemDoublePlant || stack.getItem() instanceof ItemDye || stack.getItem() instanceof ItemRecord || shouldRemove(stack.getItem().getUnlocalizedName()) ||(stack.getItem() instanceof ItemBlock && (shouldRemove(((ItemBlock) stack.getItem()).block.getUnlocalizedName()) || ((ItemBlock) stack.getItem()).block instanceof BlockPumpkin)))) ci.cancel();
    }

    private boolean shouldRemove(String blockName) {
        switch (blockName) {
            case "blockDiamond":
            case "blockIron":
            case "blockGold":
            case "blockRedstone":
            case "blockCoal":
            case "bone":
            case "diamond":
            case "ingotGold":
            case "goldNugget":
            case "slimeball":
            case "pumpkinPie":
            case "blockLapis":
                return true;
        }
        return false;
    }
}
