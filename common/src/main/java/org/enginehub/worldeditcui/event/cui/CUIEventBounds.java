package org.enginehub.worldeditcui.event.cui;

import org.enginehub.worldeditcui.event.CUIEvent;
import org.enginehub.worldeditcui.event.CUIEventArgs;
import org.enginehub.worldeditcui.event.CUIEventType;
import org.enginehub.worldeditcui.render.region.Region;

/**
 * Called when resize event is received
 * 
 * @author lahwran
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class CUIEventBounds extends CUIEvent
{
	public CUIEventBounds(CUIEventArgs args)
	{
		super(args);
	}
	
	@Override
	public CUIEventType getEventType()
	{
		return CUIEventType.MINMAX;
	}
	
	@Override
	public String raise()
	{
		Region selection = this.controller.getSelection(this.multi);
		if (selection == null)
		{
			this.controller.getDebugger().debug("No active multi selection.");
			return null;
		}
		
		int min = this.getInt(0);
		int max = this.getInt(1);
		selection.setMinMax(min, max);
		this.controller.getDebugger().debug("Expanding/contracting selection.");
		
		return null;
	}
}
