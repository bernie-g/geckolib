package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.example.client.model.entity.ExampleRenderLayerEntityModel;
import software.bernie.example.client.renderer.entity.layer.ExampleGeoLayer;
import software.bernie.example.entity.ExampleRenderLayerEntity;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib3.renderer.GeoRenderer} implementation of an entity that uses a render layer
 * @see ExampleRenderLayerEntityModel
 * @see ExampleRenderLayerEntity
 */
public class LERenderer extends GeoEntityRenderer<ExampleRenderLayerEntity> {
	public LERenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ExampleRenderLayerEntityModel());

		this.shadowRadius = 0.25f;

		// Add our render layer
		addRenderLayer(new ExampleGeoLayer(this));
    }
}
