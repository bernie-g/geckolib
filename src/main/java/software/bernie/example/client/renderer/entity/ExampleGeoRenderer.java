package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.example.client.renderer.model.entity.BatModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib.renderers.geo.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity>
{
	public ExampleGeoRenderer(EntityRendererManager renderManager)
	{
		super(renderManager, new BatModel());
	}
}
