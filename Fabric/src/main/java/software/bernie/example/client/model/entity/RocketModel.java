package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RocketModel extends AnimatedGeoModel<RocketProjectile> {
	@Override
	public Identifier getModelLocation(RocketProjectile object) {
		return EntityResources.ROCKET_MODEL;
	}

	@Override
	public Identifier getTextureLocation(RocketProjectile object) {
		return EntityResources.ROCKET_TEXTURE;
	}

	@Override
	public Identifier getAnimationFileLocation(RocketProjectile animatable) {
		return EntityResources.ROCKET_ANIMATIONS;
	}

}
