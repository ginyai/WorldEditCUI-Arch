package org.enginehub.worldeditcui.forge;

import dev.architectury.networking.NetworkManager;
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
        NetworkManager.sendToServer(CHANNEL_WECUI, codec);
    }

    public static void subscribeToCuiPacket(final NetworkManager.NetworkReceiver handler) {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CHANNEL_WECUI, handler);
    }

}
