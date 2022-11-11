package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.example.client.model.entity.RaceCarModel;
import software.bernie.example.entity.RaceCarEntity;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib3.renderer.GeoRenderer} implementation of an entity
 * @see RaceCarModel
 * @see RaceCarEntity
 */
public class RaceCarRenderer extends GeoEntityRenderer<RaceCarEntity> {
	public RaceCarRenderer(EntityRendererProvider.Context context) {
		super(context, new RaceCarModel());
	}
}