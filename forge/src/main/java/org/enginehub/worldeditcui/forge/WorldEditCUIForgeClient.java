package org.enginehub.worldeditcui.forge;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import org.enginehub.worldeditcui.WorldEditCUI;
import org.enginehub.worldeditcui.config.CUIConfiguration;
import org.enginehub.worldeditcui.event.listeners.CUIListenerChannel;
import org.enginehub.worldeditcui.event.listeners.CUIListenerWorldRender;
import org.enginehub.worldeditcui.forge.mixins.LevelRendererAccessor;
import org.enginehub.worldeditcui.forge.mixins.MinecraftAccess;
import org.enginehub.worldeditcui.render.OptifinePipelineProvider;
import org.enginehub.worldeditcui.render.PipelineProvider;
import org.enginehub.worldeditcui.render.VanillaPipelineProvider;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WorldEditCUIForgeClient {

    private static final int DELAYED_HELO_TICKS = 10;
    public static final String MOD_ID = "worldeditcui";

    private static WorldEditCUIForgeClient instance;

    private static final String KEYBIND_CATEGORY_WECUI = "key.categories.worldeditcui";
    private final Lazy<KeyMapping> keyBindToggleUI = key("toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);
    private final Lazy<KeyMapping> keyBindClearSel = key("clear", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);
    private final Lazy<KeyMapping> keyBindChunkBorder = key("chunk", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN);

    private static final List<PipelineProvider> RENDER_PIPELINES = List.of(
            new OptifinePipelineProvider(),
            new VanillaPipelineProvider()
    );

    private WorldEditCUI controller;
    private CUIListenerWorldRender worldRenderListener;
    private CUIListenerChannel channelListener;

    private Level lastWorld;
    private LocalPlayer lastPlayer;

    private boolean visible = true;
    private int delayedHelo = 0;

    /**
     * Register a key binding
     *
     * @param name id, will be used as a localization key under {@code key.worldeditcui.<name>}
     * @param type type
     * @param code default value
     * @return new, registered keybinding in the mod category
     */
    private static Lazy<KeyMapping> key(final String name, final InputConstants.Type type, final int code) {
        return Lazy.of(() -> new KeyMapping("key." + MOD_ID + '.' + name, type, code, KEYBIND_CATEGORY_WECUI));
    }

    static {
        new WorldEditCUIForgeClient().onInitialize();
    }

    public void onInitialize() {
        if (Boolean.getBoolean("wecui.debug.mixinaudit")) {
            MixinEnvironment.getCurrentEnvironment().audit();
        }

        instance = this;

        // Set up event listeners
        // CUINetworking.subscribeToCuiPacket(this::onPluginMessage);
    }


    public static class ModEventBusListener {

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(instance.keyBindToggleUI.get());
            event.register(instance.keyBindChunkBorder.get());
            event.register(instance.keyBindClearSel.get());
        }
    }

    public static class ForgeEventBusListener {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (instance.controller == null) return;
            if (event.phase == TickEvent.Phase.END) {
                getInstance().onTick(Minecraft.getInstance());
            }
        }

        @SubscribeEvent
        public static void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
            if (instance.controller == null) return;
            getInstance().onJoinGame(event.getPlayer().connection);
        }

        @SubscribeEvent
        public static void onRenderLevelStage(RenderLevelStageEvent event) {
            if (instance.controller == null) return;
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
                boolean advancedTranslucency = ((LevelRendererAccessor)event.getLevelRenderer()).getTransparencyChain() != null;

            } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {

            }
        }
    }

    public void onTick(final Minecraft mc) {
        final CUIConfiguration config = this.controller.getConfiguration();
        final boolean inGame = mc.player != null;
        final boolean clock = ((MinecraftAccess) mc).getTimer().partialTick > 0;

        if (inGame && mc.screen == null) {
            while (this.keyBindToggleUI.get().consumeClick()) {
                this.visible = !this.visible;
            }

            while (this.keyBindClearSel.get().consumeClick()) {
                if (mc.player != null) {
                    mc.player.connection.sendUnsignedCommand("/sel");
                }

                if (config.isClearAllOnKey()) {
                    this.controller.clearRegions();
                }
            }

            while (this.keyBindChunkBorder.get().consumeClick()) {
                this.controller.toggleChunkBorders();
            }
        }

        if (inGame && clock && this.controller != null) {
            if (mc.level != this.lastWorld || mc.player != this.lastPlayer) {
                this.lastWorld = mc.level;
                this.lastPlayer = mc.player;

                this.controller.getDebugger().debug("World change detected, sending new handshake");
                this.controller.clear();
                this.helo(mc.getConnection());
                this.delayedHelo = WorldEditCUIForgeClient.DELAYED_HELO_TICKS;
                if (mc.player != null && config.isPromiscuous()) {
                    mc.player.connection.sendUnsignedCommand("we cui"); // Tricks WE to send the current selection
                }
            }

            if (this.delayedHelo > 0) {
                this.delayedHelo--;
                if (this.delayedHelo == 0) {
                    this.helo(mc.getConnection());
                }
            }
        }
    }

    public void onPluginMessage(FriendlyByteBuf data) {
        try {
            final int readableBytes = data.readableBytes();
            if (readableBytes > 0) {
                final String stringPayload = data.toString(0, data.readableBytes(), StandardCharsets.UTF_8);
                Minecraft.getInstance().execute(() -> this.channelListener.onMessage(stringPayload));
            } else {
                this.getController().getDebugger().debug("Warning, invalid (zero length) payload received from server");
            }
        } catch (final Exception ex) {
            this.getController().getDebugger().info("Error decoding payload from server", ex);
        }
    }

    public void onGameInitDone(final Minecraft client) {
        this.controller = new WorldEditCUI();
        this.controller.initialise(client);
        this.worldRenderListener = new CUIListenerWorldRender(this.controller, client, RENDER_PIPELINES);
        this.channelListener = new CUIListenerChannel(this.controller);
    }

    public void onJoinGame(final ClientPacketListener handler) {
        this.visible = true;
        this.controller.getDebugger().debug("Joined game, sending initial handshake");
        this.helo(handler);
    }

    public void onPostRenderEntities(float tickDelta) {
        if (this.visible) {
            this.worldRenderListener.onRender(tickDelta);
        }
    }

    public void onWorldRenderEventAfterTranslucent(PoseStack poseStack, float partialTick, boolean advancedTranslucency) {
        if (controller == null) return;
        if (advancedTranslucency) {
            try {
                RenderSystem.getModelViewStack().pushPose();
                RenderSystem.getModelViewStack().mulPoseMatrix(poseStack.last().pose());
                RenderSystem.applyModelViewMatrix();
                Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
                getInstance().onPostRenderEntities(partialTick);
            } finally {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                RenderSystem.getModelViewStack().popPose();
            }
        }
    }

    public void onWorldRenderEventLast(PoseStack poseStack, float partialTick, boolean advancedTranslucency) {
        if (controller == null) return;
        if (!advancedTranslucency) {
            try {
                RenderSystem.getModelViewStack().pushPose();
                RenderSystem.getModelViewStack().mulPoseMatrix(poseStack.last().pose());
                RenderSystem.applyModelViewMatrix();
                getInstance().onPostRenderEntities(partialTick);
            } finally {
                RenderSystem.getModelViewStack().popPose();
                RenderSystem.applyModelViewMatrix();
            }
        }
    }

    private void helo(final ClientPacketListener handler) {
        final String message = "v|" + WorldEditCUI.PROTOCOL_VERSION;
        final ByteBuf buffer = Unpooled.copiedBuffer(message, StandardCharsets.UTF_8);
        CUINetworking.send(handler, new FriendlyByteBuf(buffer));
    }

    public WorldEditCUI getController()
    {
        return this.controller;
    }

    public static WorldEditCUIForgeClient getInstance() {
        return instance;
    }
}
