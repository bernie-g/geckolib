package anightdazingzoroark.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import anightdazingzoroark.example.client.model.entity.ExampleEntityModel;
import anightdazingzoroark.example.entity.GeoExampleEntity;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityRenderer;

public class ExampleGeoRenderer extends GeoEntityRenderer<GeoExampleEntity> {

	public ExampleGeoRenderer(RenderManager renderManager) {
		super(renderManager, new ExampleEntityModel());
	}
}
