package org.enginehub.worldeditcui.forge;


import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;

import java.util.function.Predicate;

final class PacketHandlerUtil {
    private PacketHandlerUtil() {
    }

    static NetworkRegistry.ChannelBuilder buildLenientHandler(String id, int protocolVersion) {
        final String verStr = Integer.toString(protocolVersion);
        final Predicate<String> validator = validateLenient(verStr);
        return NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation("worldedit", id))
                .clientAcceptedVersions(validator)
                .serverAcceptedVersions(validator)
                .networkProtocolVersion(() -> verStr);
    }

    private static Predicate<String> validateLenient(String protocolVersion) {
        return remoteVersion ->
                protocolVersion.equals(remoteVersion)
                        || NetworkRegistry.ABSENT.version().equals(remoteVersion)
                        || NetworkRegistry.ACCEPTVANILLA.equals(remoteVersion);
    }
}