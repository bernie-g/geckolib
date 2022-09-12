package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class RocketModel extends AnimatedGeoModel<RocketProjectile> {
	@Override
	public Identifier getModelResource(RocketProjectile object) {
		return new Identifier(GeckoLib.ModID, "geo/rocket.geo.json");
	}

	@Override
	public Identifier getTextureResource(RocketProjectile object) {
		return new Identifier(GeckoLib.ModID, "textures/entity/projectiles/rocket.png");
	}

	@Override
	public Identifier getAnimationResource(RocketProjectile animatable) {
		return new Identifier(GeckoLib.ModID, "animations/rocket.animation.json");
	}

}
