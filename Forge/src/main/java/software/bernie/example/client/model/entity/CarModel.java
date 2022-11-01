package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.CarEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CarModel extends AnimatedGeoModel<CarEntity> {
	@Override
	public ResourceLocation getAnimationFileLocation(CarEntity entity) {
		return EntityResources.CAR_ANIMATIONS;
	}

	@Override
	public ResourceLocation getModelLocation(CarEntity entity) {
		return EntityResources.CAR_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(CarEntity entity) {
		return EntityResources.CAR_TEXTURE;
	}
}