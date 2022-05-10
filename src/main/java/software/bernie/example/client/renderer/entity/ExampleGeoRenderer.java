package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.example.client.model.entity.ExampleEntityModel;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {

	public ExampleGeoRenderer(RenderManager renderManager) {
		super(renderManager, new ExampleEntityModel());
	}
}
