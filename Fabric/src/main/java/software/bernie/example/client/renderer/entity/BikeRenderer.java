package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.example.client.model.entity.BikeModel;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib.renderer.GeoRenderer} for {@link BikeEntity}
 * @see BikeModel
 */
public class BikeRenderer extends GeoEntityRenderer<BikeEntity> {
	public BikeRenderer(EntityRendererProvider.Context context) {
		super(context, new BikeModel());
	}
}
