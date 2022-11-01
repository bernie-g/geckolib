package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class RocketModel extends AnimatedGeoModel<RocketProjectile> {
	@Override
	public ResourceLocation getModelResource(RocketProjectile object) {
		return EntityResources.ROCKET_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(RocketProjectile object) {
		return EntityResources.ROCKET_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(RocketProjectile animatable) {
		return EntityResources.ROCKET_ANIMATIONS;
	}

}
