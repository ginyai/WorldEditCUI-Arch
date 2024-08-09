package org.enginehub.worldeditcui.forge.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.PostChain;
import org.enginehub.worldeditcui.forge.WorldEditCUIForgeClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//No longer needs remapping, as Forge now uses official mappings at runtime.
@Mixin(value = LevelRenderer.class, remap = false)
public class LevelRendererMixin {

    @Shadow private PostChain transparencyChain;
    @Unique private boolean didRenderParticles;
    @Unique private PoseStack matrixStack;
    @Unique private float partialTick;

    @ModifyVariable(method = "renderLevel", at = @At(value = "STORE"), ordinal = 0)
    private PoseStack onCreatePoseStack(PoseStack poseStack) {
        this.matrixStack = poseStack;
        return poseStack;
    }

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void beforeRender(float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {

        this.partialTick = tickDelta;
        didRenderParticles = false;
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V"
            )
    )
    private void onRenderParticles(CallbackInfo ci) {
        // set a flag so we know the next pushMatrix call is after particles
        didRenderParticles = true;
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"
            )
    )
    private void beforeClouds(CallbackInfo ci) {
        if (didRenderParticles) {
            didRenderParticles = false;
            WorldEditCUIForgeClient.getInstance().onWorldRenderEventAfterTranslucent(matrixStack, partialTick, transparencyChain != null);
        }
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/Camera;)V"
            )
    )
    private void onChunkDebugRender(CallbackInfo ci) {
        WorldEditCUIForgeClient.getInstance().onWorldRenderEventLast(matrixStack, partialTick, transparencyChain != null);
    }
}
