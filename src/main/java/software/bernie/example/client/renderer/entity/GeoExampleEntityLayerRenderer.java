package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.example.client.layer.GeoExampleLayer;
import software.bernie.example.client.model.entity.GeoExampleEntityLayerModel;
import software.bernie.example.entity.GeoExampleEntityLayer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GeoExampleEntityLayerRenderer extends GeoEntityRenderer<GeoExampleEntityLayer> {

	@SuppressWarnings("unchecked")
	public GeoExampleEntityLayerRenderer(RenderManager renderManager) {
		super(renderManager, new GeoExampleEntityLayerModel());
        this.addLayer(new GeoExampleLayer(this));
        this.shadowSize = 0.2f;
	}

}
