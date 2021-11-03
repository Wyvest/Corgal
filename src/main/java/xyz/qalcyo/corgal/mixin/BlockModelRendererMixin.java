package xyz.qalcyo.corgal.mixin;

import net.minecraft.block.BlockColored;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.corgal.utils.ColorUtils;
import xyz.qalcyo.corgal.utils.HypixelUtils;

import java.util.List;

//TODO: fix for non-smooth lighting
@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {
    private boolean shouldModifyColor = false;
    private boolean isClay;
    private MapColor mapColor;

    @Inject(method = "renderQuadsSmooth", at = @At("HEAD"), remap = false)
    private void setVariables(IBlockAccess worldIn, IBlockState stateIn, BlockPos blockPosIn, WorldRenderer buffer, List<BakedQuad> list, @Coerce Object renderEnv, CallbackInfo ci) {
        int height = HypixelUtils.getHeight();
        if (height == -1) {
            shouldModifyColor = false;
            return;
        }
        shouldModifyColor = CorgalConfig.heightOverlay && blockPosIn.getY() == (height - 1) && stateIn.getBlock() instanceof BlockColored;
        if (shouldModifyColor) {
            isClay = stateIn.getBlock().getMaterial() == Material.rock;
            mapColor = stateIn.getBlock().getMapColor(worldIn.getBlockState(blockPosIn));
        }
    }

    @ModifyArgs(method = "renderQuadsSmooth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;putColorMultiplier(FFFI)V", remap = true), remap = false)
    private void modifyArgs(Args args) {
        if (shouldModifyColor && mapColor != null && (!isClay || check(mapColor.colorIndex))) {
            int color = ColorUtils.getCachedDarkColor(mapColor);
            args.set(0, (float) ColorUtils.getRed(color) / 255);
            args.set(1, (float) ColorUtils.getGreen(color) / 255);
            args.set(2, (float) ColorUtils.getBlue(color) / 255);
        }
    }

    @ModifyArgs(method = "renderQuadsSmooth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;putColorMultiplierRgba(FFFFI)V", remap = false), remap = false)
    private void modifyArg4s(Args args) {
        if (shouldModifyColor && mapColor != null && (!isClay || check(mapColor.colorIndex))) {
            int color = ColorUtils.getCachedDarkColor(mapColor);
            args.set(0, (float) ColorUtils.getRed(color) / 255);
            args.set(1, (float) ColorUtils.getGreen(color) / 255);
            args.set(2, (float) ColorUtils.getBlue(color) / 255);
        }
    }

    @Inject(method = "renderQuadsFlat", at = @At("HEAD"), remap = false)
    private void setVariables2(IBlockAccess worldIn, IBlockState stateIn, BlockPos blockPosIn, EnumFacing face, int brightnessIn, boolean ownBrightness, WorldRenderer buffer, List<BakedQuad> list, @Coerce Object renderEnv, CallbackInfo ci) {
        int height = HypixelUtils.getHeight();
        if (height == -1) {
            shouldModifyColor = false;
            return;
        }
        shouldModifyColor = CorgalConfig.heightOverlay && blockPosIn.getY() == (height - 1) && stateIn.getBlock() instanceof BlockColored;
        if (shouldModifyColor) {
            isClay = stateIn.getBlock().getMaterial() == Material.rock;
            mapColor = stateIn.getBlock().getMapColor(worldIn.getBlockState(blockPosIn));
        }
    }

    @ModifyArgs(method = "renderQuadsFlat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;putColorMultiplier(FFFI)V", remap = true), remap = false)
    private void modifyArg2s(Args args) {
        if (shouldModifyColor && mapColor != null && (!isClay || check(mapColor.colorIndex))) {
            int color = ColorUtils.getCachedDarkColor(mapColor);
            args.set(0, (float) ColorUtils.getRed(color) / 255);
            args.set(1, (float) ColorUtils.getGreen(color) / 255);
            args.set(2, (float) ColorUtils.getBlue(color) / 255);
        }
    }

    private boolean check(int color) {
        switch (color) {
            case 18:
            case 25:
            case 27:
            case 28:
                return true;
            default:
                return false;
        }
    }
}
