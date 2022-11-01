package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel<BikeEntity> {
	@Override
	public Identifier getAnimationFileLocation(BikeEntity entity) {
		return EntityResources.BIKE_ANIMATIONS;
	}

	@Override
	public Identifier getModelLocation(BikeEntity entity) {
		return EntityResources.BIKE_MODEL;
	}

	@Override
	public Identifier getTextureLocation(BikeEntity entity) {
		return EntityResources.BIKE_TEXTURE;
	}
}