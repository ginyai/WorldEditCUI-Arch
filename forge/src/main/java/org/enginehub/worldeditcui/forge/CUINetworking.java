package org.enginehub.worldeditcui.forge;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.EventNetworkChannel;
import net.minecraftforge.network.PacketDistributor;
import org.enginehub.worldeditcui.network.CUIEventPayload;

import java.util.function.BiConsumer;

import static net.minecraftforge.network.Channel.VersionTest.*;

/**
 * Networking wrappers to integrate nicely with MultiConnect.
 *
 * <p>These methods generally first call </p>
 */
public final class CUINetworking {

    public static final ResourceLocation CUI_PACKET_NAME = new ResourceLocation("worldedit", "cui");
    private static EventNetworkChannel channel;

    private CUINetworking() {
    }

    public static void send(final CUIEventPayload pkt) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        pkt.encode(byteBuf);
        channel.send(byteBuf, PacketDistributor.SERVER.noArg());
    }

    public static void subscribeToCuiPacket(final BiConsumer<CUIEventPayload, CustomPayloadEvent.Context> handler) {
        channel.addListener(e ->{
            try {
                handler.accept(CUIEventPayload.decode(e.getPayload()), e.getSource());
            } catch (Exception ex) {
                WorldEditCUIForge.LOGGER.error("Error decoding payload from server", ex);
            }
        });
    }

    public static void init(int protocolVersion) {
        channel = ChannelBuilder.named(CUI_PACKET_NAME)
                .networkProtocolVersion(protocolVersion)
                .acceptedVersions(exact(protocolVersion))
                .optional()
                .eventNetworkChannel();
    }
}
