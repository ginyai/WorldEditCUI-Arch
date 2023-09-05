package org.enginehub.worldeditcui.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.enginehub.worldeditcui.WorldEditCUI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = WorldEditCUIForge.MOD_ID)
public class WorldEditCUIForge {

    public static final String MOD_ID = "worldeditcui";

    public WorldEditCUIForge() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.register(WorldEditCUIForgeClient.ModEventBusListener.class);
            MinecraftForge.EVENT_BUS.register(WorldEditCUIForgeClient.ForgeEventBusListener.class);
        });
    }
}