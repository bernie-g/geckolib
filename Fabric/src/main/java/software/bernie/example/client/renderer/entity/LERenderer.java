package software.bernie.example.client.renderer.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import software.bernie.example.client.model.entity.LEModel;
import software.bernie.example.client.renderer.entity.layer.GeoExampleLayer;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class LERenderer extends GeoEntityRenderer<LEEntity> {

	public LERenderer(EntityRenderDispatcher renderDispatcher) {
		super(renderDispatcher, new LEModel());
		this.addLayer(new GeoExampleLayer(this));
		this.shadowRadius = 0.25f;
	}
	
}
