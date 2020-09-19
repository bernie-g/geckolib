package software.bernie.geckolib.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.client.renderer.model.entity.ExampleGeoModel;
import software.bernie.geckolib.example.entity.GeoExampleEntity;
import software.bernie.geckolib.geo.render.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity>
{
	public ExampleGeoRenderer(EntityRendererManager renderManager)
	{
		super(renderManager, new ExampleGeoModel());
	}
}
