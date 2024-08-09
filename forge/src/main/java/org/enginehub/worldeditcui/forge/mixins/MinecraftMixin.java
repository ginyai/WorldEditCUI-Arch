package org.enginehub.worldeditcui.forge.mixins;

import net.minecraft.client.Minecraft;
import org.enginehub.worldeditcui.forge.WorldEditCUIForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;gameThread:Ljava/lang/Thread;", shift = At.Shift.AFTER, ordinal = 0), method = "run")
    private void onStart(CallbackInfo ci) {
        WorldEditCUIForgeClient.getInstance().onGameInitDone((Minecraft)(Object) this);
    }
}
