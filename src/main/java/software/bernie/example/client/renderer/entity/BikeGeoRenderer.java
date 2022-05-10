package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.example.client.model.entity.BikeModel;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BikeGeoRenderer extends GeoEntityRenderer<BikeEntity> {
	public BikeGeoRenderer(RenderManager renderManager) {
		super(renderManager, new BikeModel());
	}
}
