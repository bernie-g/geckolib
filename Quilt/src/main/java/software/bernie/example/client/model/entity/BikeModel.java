package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel<BikeEntity> {
	@Override
	public ResourceLocation getAnimationResource(BikeEntity entity) {
		return EntityResources.BIKE_ANIMATIONS;
	}

	@Override
	public ResourceLocation getModelResource(BikeEntity entity) {
		return EntityResources.BIKE_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(BikeEntity entity) {
		return EntityResources.BIKE_TEXTURE;
	}
}