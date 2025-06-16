package anightdazingzoroark.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import anightdazingzoroark.example.client.model.entity.LEModel;
import anightdazingzoroark.example.client.renderer.entity.layer.GeoExampleLayer;
import anightdazingzoroark.example.entity.GeoExampleEntityLayer;
import anightdazingzoroark.riftlib.renderers.geo.GeoEntityRenderer;

public class LERenderer extends GeoEntityRenderer<GeoExampleEntityLayer> {

	@SuppressWarnings("unchecked")
	public LERenderer(RenderManager renderManager) {
		super(renderManager, new LEModel());
		this.addLayer(new GeoExampleLayer(this));
		this.shadowSize = 0.2f;
	}

}
