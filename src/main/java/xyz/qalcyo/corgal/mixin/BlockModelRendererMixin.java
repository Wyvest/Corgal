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
import net.optifine.render.RenderEnv;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qalcyo.corgal.config.CorgalConfig;
import xyz.qalcyo.corgal.utils.ColorUtils;
import xyz.qalcyo.corgal.utils.HypixelUtils;

import java.util.List;

@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {

    @Dynamic("OptiFine implements its own version of renderModelAmbientOcclusionQuads")
    @ModifyArgs(method = {"renderQuadsSmooth", "renderModelAmbientOcclusionQuads"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;putColorMultiplier(FFFI)V", remap = true))
    private void modifyArgs(Args args, IBlockAccess worldIn, IBlockState stateIn, BlockPos blockPosIn, WorldRenderer buffer, List<BakedQuad> list, RenderEnv renderEnv) {
        if (CorgalConfig.heightOverlay && stateIn.getBlock() instanceof BlockColored) {
            int height = HypixelUtils.height;
            if (height == -1) {
                return;
            }
            MapColor mapColor = stateIn.getBlock().getMapColor(worldIn.getBlockState(blockPosIn));
            if (blockPosIn.getY() == (height - 1) && mapColor != null && (!(stateIn.getBlock().getMaterial() == Material.rock) || check(mapColor.colorIndex))) {
                int color = ColorUtils.getCachedDarkColor(mapColor);
                args.set(0, (float) ColorUtils.getRed(color) / 255);
                args.set(1, (float) ColorUtils.getGreen(color) / 255);
                args.set(2, (float) ColorUtils.getBlue(color) / 255);
            }
        }
    }

    @Dynamic("OptiFine implements its own version of putColorMultiplierRgba")
    @ModifyArgs(method = "renderQuadsSmooth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;putColorMultiplierRgba(FFFFI)V", remap = false), remap = false)
    private void modifyArg4s(Args args, IBlockAccess worldIn, IBlockState stateIn, BlockPos blockPosIn, WorldRenderer buffer, List<BakedQuad> list, RenderEnv renderEnv) {
        if (CorgalConfig.heightOverlay && stateIn.getBlock() instanceof BlockColored) {
            int height = HypixelUtils.height;
            if (height == -1) {
                return;
            }
            MapColor mapColor = stateIn.getBlock().getMapColor(worldIn.getBlockState(blockPosIn));
            if (blockPosIn.getY() == (height - 1) && mapColor != null && (!(stateIn.getBlock().getMaterial() == Material.rock) || check(mapColor.colorIndex))) {
                int color = ColorUtils.getCachedDarkColor(mapColor);
                args.set(0, (float) ColorUtils.getRed(color) / 255);
                args.set(1, (float) ColorUtils.getGreen(color) / 255);
                args.set(2, (float) ColorUtils.getBlue(color) / 255);
            }
        }
    }

    @Dynamic("OptiFine implements its own version of renderQuadsFlat")
    @ModifyArgs(method = {"renderQuadsFlat", "renderModelStandardQuads"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;putColorMultiplier(FFFI)V", remap = true))
    private void modifyArg2s(Args args, IBlockAccess worldIn, IBlockState stateIn, BlockPos blockPosIn, EnumFacing face, int brightnessIn, boolean ownBrightness, WorldRenderer buffer, List<BakedQuad> list, RenderEnv renderEnv) {
        if (CorgalConfig.heightOverlay && stateIn.getBlock() instanceof BlockColored) {
            int height = HypixelUtils.height;
            if (height == -1) {
                return;
            }
            MapColor mapColor = stateIn.getBlock().getMapColor(worldIn.getBlockState(blockPosIn));
            if (blockPosIn.getY() == (height - 1) && mapColor != null && (!(stateIn.getBlock().getMaterial() == Material.rock) || check(mapColor.colorIndex))) {
                int color = ColorUtils.getCachedDarkColor(mapColor);
                args.set(0, (float) ColorUtils.getRed(color) / 255);
                args.set(1, (float) ColorUtils.getGreen(color) / 255);
                args.set(2, (float) ColorUtils.getBlue(color) / 255);
            }
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
