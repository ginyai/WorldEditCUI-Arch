package org.enginehub.worldeditcui.render.shapes;

import org.enginehub.worldeditcui.event.listeners.CUIRenderContext;
import org.enginehub.worldeditcui.render.LineStyle;
import org.enginehub.worldeditcui.render.RenderStyle;
import org.enginehub.worldeditcui.util.Vector3;

/**
 * Draws a polygon
 * 
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public class Render3DPolygon extends RenderRegion
{
	private final Vector3[] vertices;
	
	public Render3DPolygon(RenderStyle style, Vector3... vertices)
	{
		super(style);
		this.vertices = vertices;
	}
	
	@Override
	public void render(CUIRenderContext ctx)
	{
		for (LineStyle line : this.style.getLines())
		{
			if (!ctx.apply(line, this.style.getRenderType()))
			{
				continue;
			}

			ctx.beginLineLoop()
					.color(line);
			for (int i = 0; i < this.vertices.length; i++)
			{
				final Vector3 vertex = this.vertices[i];
				ctx.vertex(vertex.getX() - ctx.cameraPos().getX(), vertex.getY() - ctx.cameraPos().getY(), vertex.getZ() - ctx.cameraPos().getZ());
			}
			ctx.endLineLoop();
		}
	}
}
