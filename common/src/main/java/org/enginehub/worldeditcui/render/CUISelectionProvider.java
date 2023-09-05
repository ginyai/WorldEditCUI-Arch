package org.enginehub.worldeditcui.render;

import org.enginehub.worldeditcui.InitialisationFactory;
import org.enginehub.worldeditcui.WorldEditCUI;
import org.enginehub.worldeditcui.exceptions.InitialisationException;
import org.enginehub.worldeditcui.render.region.Region;
import org.enginehub.worldeditcui.render.region.RegionType;

/**
 * @author Adam Mummery-Smith
 */
public class CUISelectionProvider implements InitialisationFactory
{

	private final WorldEditCUI controller;
	
	public CUISelectionProvider(WorldEditCUI controller)
	{
		this.controller = controller;
	}

	@Override
	public void initialise() throws InitialisationException
	{
	}
	
	public Region createSelection(String key)
	{
		if ("clear".equals(key))
		{
			return null;
		}

		final RegionType type = RegionType.named(key);

		if (type != null) {
			try {
				return type.make(this.controller);
			} catch (Exception ex) {
				this.controller.getDebugger().debug("Error creating " + key + " selection: " + ex.getClass().getSimpleName() + " " + ex.getMessage());
			}
		}

		return null;
	}
}
