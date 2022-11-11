package software.bernie.example.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.example.client.model.entity.ParasiteModel;
import software.bernie.example.entity.ParasiteEntity;
import software.bernie.geckolib3.renderer.GeoEntityRenderer;

/**
 * Example {@link software.bernie.geckolib3.renderer.GeoRenderer} implementation of an entity
 * @see ParasiteModel
 * @see ParasiteEntity
 */
public class ParasiteRenderer extends GeoEntityRenderer<ParasiteEntity> {
	public ParasiteRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new ParasiteModel());
	}
}
