package org.enginehub.worldeditcui.render.shapes;

import org.enginehub.worldeditcui.event.listeners.CUIRenderContext;
import org.enginehub.worldeditcui.render.RenderStyle;
import org.enginehub.worldeditcui.util.Observable;
import org.enginehub.worldeditcui.util.Observer;

/**
 * Base class for region renderers
 * 
 * @author Adam Mummery-Smith
 */
public abstract class RenderRegion implements Observer
{
	protected static final double OFFSET = 0.001d; // to avoid z-fighting with blocks

	protected RenderStyle style;
	
	protected RenderRegion(RenderStyle style)
	{
		this.style = style;
	}

	public final void setStyle(RenderStyle style)
	{
		this.style = style;
	}
	
	public abstract void render(CUIRenderContext ctx);
	
	@Override
	public void notifyChanged(Observable<?> source)
	{
		
	}
}
