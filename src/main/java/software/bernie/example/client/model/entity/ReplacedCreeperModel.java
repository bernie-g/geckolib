package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@SuppressWarnings("rawtypes")
public class ReplacedCreeperModel extends AnimatedGeoModel {
	@Override
	public Identifier getModelResource(Object object) {
		return new Identifier(GeckoLib.ModID, "geo/creeper.geo.json");
	}

	@Override
	public Identifier getTextureResource(Object object) {
		return new Identifier(GeckoLib.ModID, "textures/model/entity/creeper.png");
	}

	@Override
	public Identifier getAnimationResource(Object animatable) {
		return new Identifier(GeckoLib.ModID, "animations/creeper.animation.json");
	}
}