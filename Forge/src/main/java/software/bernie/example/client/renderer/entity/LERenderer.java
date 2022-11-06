package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.example.client.model.entity.LEModel;
import software.bernie.example.client.renderer.entity.layer.ExampleGeoLayer;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

public class LERenderer extends GeoEntityRenderer<LEEntity> {
	public LERenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LEModel());

		this.shadowRadius = 0.25f;

        addLayer(new ExampleGeoLayer(this));
    }
}
