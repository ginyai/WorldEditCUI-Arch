package org.enginehub.worldeditcui.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Consumer;

/**
 * Networking wrappers to integrate nicely with MultiConnect.
 *
 * <p>These methods generally first call </p>
 */
public final class CUINetworking {

    public static final ResourceLocation CUI_PACKET_NAME = new ResourceLocation("worldedit", "cui");

    private CUINetworking() {
    }

    public static void send(final ClientPacketListener handler, final FriendlyByteBuf codec) {
        Minecraft.getInstance().getConnection().getConnection()
                        .send(new ServerboundCustomPayloadPacket(CUI_PACKET_NAME, codec));
    }

    public static void subscribeToCuiPacket(final Consumer<NetworkEvent.ServerCustomPayloadEvent> handler) {

    }

}
