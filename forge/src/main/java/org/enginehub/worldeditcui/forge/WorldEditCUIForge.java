package org.enginehub.worldeditcui.forge;

import org.enginehub.worldeditcui.WorldEditCUI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WorldEditCUI.MOD_ID)
public class WorldEditCUIForge {
    public WorldEditCUIForge() {
        WorldEditCUI.init();
    }
}