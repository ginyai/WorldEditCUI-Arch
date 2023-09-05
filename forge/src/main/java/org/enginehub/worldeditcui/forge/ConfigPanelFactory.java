package org.enginehub.worldeditcui.forge;

import net.minecraftforge.client.ConfigScreenHandler;
import org.enginehub.worldeditcui.gui.CUIConfigPanel;

/**
 * @author Mark Vainomaa
 */
public final class ConfigPanelFactory {

    public static ConfigScreenHandler.ConfigScreenFactory getFactory() {
        return new ConfigScreenHandler.ConfigScreenFactory((mc, screen) ->
                new CUIConfigPanel(screen, WorldEditCUIForgeClient.getInstance().getController().getConfiguration()));
    }
}
