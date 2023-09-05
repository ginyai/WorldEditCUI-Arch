package org.enginehub.worldeditcui.render.region;

import org.enginehub.worldeditcui.WorldEditCUI;
import org.enginehub.worldeditcui.event.listeners.CUIRenderContext;
import org.enginehub.worldeditcui.render.ConfiguredColour;
import org.enginehub.worldeditcui.render.points.PointCube;
import org.enginehub.worldeditcui.render.shapes.RenderEllipsoid;
import org.enginehub.worldeditcui.util.Vector3;

/**
 * Main controller for a ellipsoid-type region
 * 
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public class EllipsoidRegion extends Region
{
	private PointCube centre;
	private Vector3 radii;
	
	private RenderEllipsoid ellipsoid;
	
	public EllipsoidRegion(WorldEditCUI controller)
	{
		super(controller, ConfiguredColour.ELLIPSOIDGRID.style(), ConfiguredColour.ELLIPSOIDCENTRE.style());
	}
	
	@Override
	public void render(CUIRenderContext ctx)
	{
		if (this.centre != null && this.radii != null)
		{
			this.centre.render(ctx);
			this.ellipsoid.render(ctx);
		}
		else if (this.centre != null)
		{
			this.centre.render(ctx);
		}
	}
	
	@Override
	public void setEllipsoidCenter(int x, int y, int z)
	{
		this.centre = new PointCube(x, y, z);
		this.centre.setStyle(this.styles[1]);
		this.update();
	}
	
	@Override
	public void setEllipsoidRadii(double x, double y, double z)
	{
		this.radii = new Vector3(x, y, z);
		this.update();
	}

	private void update()
	{
		if (this.centre != null && this.radii != null)
		{
			this.ellipsoid = new RenderEllipsoid(this.styles[0], this.centre, this.radii);
		}
	}
	
	@Override
	protected void updateStyles()
	{
		if (this.ellipsoid != null)
		{
			this.ellipsoid.setStyle(this.styles[0]);
		}
		
		if (this.centre != null)
		{
			this.centre.setStyle(this.styles[1]);
		}
	}

	@Override
	public RegionType getType()
	{
		return RegionType.ELLIPSOID;
	}
	
}
