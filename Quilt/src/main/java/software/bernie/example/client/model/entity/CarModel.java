package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.CarEntity;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class CarModel extends AnimatedGeoModel<CarEntity> {
	@Override
	public ResourceLocation getAnimationResource(CarEntity entity) {
		return EntityResources.CAR_ANIMATIONS;
	}

	@Override
	public ResourceLocation getModelResource(CarEntity entity) {
		return EntityResources.CAR_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(CarEntity entity) {
		return EntityResources.CAR_TEXTURE;
	}
}