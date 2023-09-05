package org.enginehub.worldeditcui.fabric;

import org.enginehub.worldeditcui.WorldEditCUI;
import net.fabricmc.api.ModInitializer;

public class WorldEditCUIFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        WorldEditCUI.init();
    }
}