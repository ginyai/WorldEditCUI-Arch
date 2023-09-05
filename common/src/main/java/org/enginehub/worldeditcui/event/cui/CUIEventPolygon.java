package org.enginehub.worldeditcui.event.cui;

import org.enginehub.worldeditcui.event.CUIEvent;
import org.enginehub.worldeditcui.event.CUIEventArgs;
import org.enginehub.worldeditcui.event.CUIEventType;
import org.enginehub.worldeditcui.render.region.Region;

/**
 * Called when polygon event is received
 * 
 * @author lahwran
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class CUIEventPolygon extends CUIEvent
{
	public CUIEventPolygon(CUIEventArgs args)
	{
		super(args);
	}
	
	@Override
	public CUIEventType getEventType()
	{
		return CUIEventType.POLYGON;
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

		final int[] vertexIds = new int[this.params.length];
		for (int i = 0; i < this.params.length; ++i)
		{
			vertexIds[i] = this.getInt(i);
		}
		
		selection.addPolygon(vertexIds);
		//this.controller.getDebugger().debug("Setting point #" + id);
		
		return null;
	}
}
