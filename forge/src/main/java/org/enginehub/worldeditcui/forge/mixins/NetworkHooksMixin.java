package org.enginehub.worldeditcui.forge.mixins;

import net.minecraft.network.Connection;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import org.enginehub.worldeditcui.forge.CUINetworking;
import org.enginehub.worldeditcui.forge.WorldEditCUIForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetworkHooks.class, remap = false)
public class NetworkHooksMixin {

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private static void onCustomPayload(ICustomPacket<?> packet, Connection manager, CallbackInfoReturnable<Boolean> cir) {
        if (packet.getDirection() == NetworkDirection.PLAY_TO_CLIENT
                && packet.getName().equals(CUINetworking.CUI_PACKET_NAME)) {
            WorldEditCUIForgeClient.getInstance().onPluginMessage(packet.getInternalData());
            cir.setReturnValue(true);
        }
    }
}
