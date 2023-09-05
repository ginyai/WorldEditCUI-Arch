package org.enginehub.worldeditcui.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Networking wrappers to integrate nicely with MultiConnect.
 *
 * <p>These methods generally first call </p>
 */
final class CUINetworking {

    private static final String CHANNEL_LEGACY = "WECUI"; // pre-1.13 channel name
    public static final ResourceLocation CHANNEL_WECUI = new ResourceLocation("worldedit", "cui");

    private CUINetworking() {
    }

    public static void send(final ClientPacketListener handler, final FriendlyByteBuf codec) {
        ClientPlayNetworking.send(CHANNEL_WECUI, codec);
    }

    public static void subscribeToCuiPacket(final ClientPlayNetworking.PlayChannelHandler handler) {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_WECUI, handler);
    }

}
