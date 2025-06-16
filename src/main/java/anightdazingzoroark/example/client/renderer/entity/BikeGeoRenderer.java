package anightdazingzoroark.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import anightdazingzoroark.example.client.model.entity.BikeModel;
import anightdazingzoroark.example.entity.BikeEntity;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityRenderer;

public class BikeGeoRenderer extends GeoEntityRenderer<BikeEntity> {
	public BikeGeoRenderer(RenderManager renderManager) {
		super(renderManager, new BikeModel());
	}
}
