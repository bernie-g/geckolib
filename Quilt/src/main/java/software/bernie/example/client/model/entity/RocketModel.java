package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RocketModel extends AnimatedGeoModel<RocketProjectile> {
	@Override
	public ResourceLocation getModelResource(RocketProjectile object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/rocket.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RocketProjectile object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/entity/projectiles/rocket.png");
	}

	@Override
	public ResourceLocation getAnimationResource(RocketProjectile animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/rocket.animation.json");
	}

}
