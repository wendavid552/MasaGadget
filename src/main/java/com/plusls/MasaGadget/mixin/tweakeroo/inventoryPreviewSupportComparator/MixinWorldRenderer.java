package com.plusls.MasaGadget.mixin.tweakeroo.inventoryPreviewSupportComparator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.MasaGadget.ModInfo;
import com.plusls.MasaGadget.config.Configs;
import com.plusls.MasaGadget.util.HitResultUtil;
import com.plusls.MasaGadget.util.RenderUtil;
import fi.dy.masa.tweakeroo.config.FeatureToggle;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.compat.minecraft.network.chat.ComponentCompatApi;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;

@Dependencies(and = @Dependency(ModInfo.TWEAKEROO_MOD_ID))
@Mixin(LevelRenderer.class)
public class MixinWorldRenderer {

    //#if MC > 11404
    @Inject(method = "renderLevel", at = @At(value = "RETURN"))
    private void postRender(PoseStack matrices, float tickDelta, long limitTime,
                            boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                            LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {

        if (!FeatureToggle.TWEAK_INVENTORY_PREVIEW.getBooleanValue() || !Hotkeys.INVENTORY_PREVIEW.getKeybind().isKeybindHeld() ||
                !Configs.inventoryPreviewSupportComparator) {
            return;
        }


        // 开始渲染
        BlockPos pos = HitResultUtil.getHitBlockPos();
        Object blockEntity = HitResultUtil.getLastHitBlockEntity();

        if (pos != null && blockEntity instanceof ComparatorBlockEntity) {
            Component literalText = ComponentCompatApi.literal(((ComparatorBlockEntity) blockEntity).getOutputSignal() + "")
                    .withStyle(ChatFormatting.GREEN);
            // 不加 1.17 渲染会有问题
            RenderSystem.disableDepthTest();
            RenderUtil.renderTextOnWorld(matrices, camera, pos, literalText, true);
            RenderSystem.enableDepthTest();
        }
    }
    //#endif
}
