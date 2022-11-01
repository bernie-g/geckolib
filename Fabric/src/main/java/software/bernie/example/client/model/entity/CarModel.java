package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.CarEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CarModel extends AnimatedGeoModel<CarEntity> {
	@Override
	public Identifier getAnimationFileLocation(CarEntity entity) {
		return EntityResources.CAR_ANIMATIONS;
	}

	@Override
	public Identifier getModelLocation(CarEntity entity) {
		return EntityResources.CAR_MODEL;
	}

	@Override
	public Identifier getTextureLocation(CarEntity entity) {
		return EntityResources.CAR_TEXTURE;
	}
}