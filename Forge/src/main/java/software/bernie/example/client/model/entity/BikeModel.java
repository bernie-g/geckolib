package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel<BikeEntity> {
	@Override
	public ResourceLocation getAnimationFileLocation(BikeEntity entity) {
		return EntityResources.BIKE_ANIMATIONS;
	}

	@Override
	public ResourceLocation getModelLocation(BikeEntity entity) {
		return EntityResources.BIKE_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(BikeEntity entity) {
		return EntityResources.BIKE_TEXTURE;
	}
}