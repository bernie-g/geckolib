package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.example.client.model.entity.LEModel;
import software.bernie.example.client.renderer.entity.layer.GeoExampleLayer;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class LERenderer extends GeoEntityRenderer<LEEntity> {

	public LERenderer(EntityRendererManager renderManager) {
		super(renderManager, new LEModel());
		this.addLayer(new GeoExampleLayer(this));
		this.shadowRadius = 0.25f;
	}
}
