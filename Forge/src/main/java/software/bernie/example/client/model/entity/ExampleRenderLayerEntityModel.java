package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.ExampleRenderLayerEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.DefaultedEntityGeoModel;
import software.bernie.geckolib3.model.GeoModel;

/**
 * Example {@link GeoModel} for the {@link ExampleRenderLayerEntity}
 * @see software.bernie.example.client.renderer.entity.ExampleExtendedRendererEntityRenderer
 * @see software.bernie.example.client.renderer.entity.layer.ExampleGeoLayer
 */
public class ExampleRenderLayerEntityModel extends DefaultedEntityGeoModel<ExampleRenderLayerEntity> {
	public ExampleRenderLayerEntityModel() {
		super(new ResourceLocation(GeckoLib.MOD_ID, "layer_entity"));
	}
}