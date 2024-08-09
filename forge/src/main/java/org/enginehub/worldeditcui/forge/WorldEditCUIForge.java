package org.enginehub.worldeditcui.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = WorldEditCUIForge.MOD_ID)
public class WorldEditCUIForge {
    public static final String MOD_ID = "worldeditcui";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public WorldEditCUIForge() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    ConfigPanelFactory::getFactory);

            eventBus.register(WorldEditCUIForgeClient.ModEventBusListener.class);
            MinecraftForge.EVENT_BUS.register(WorldEditCUIForgeClient.ForgeEventBusListener.class);
        });
    }
}