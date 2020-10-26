package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class ReplacedCreeperModel extends AnimatedGeoModel {

	@Override
	public Identifier getModelLocation(Object object) {
		return new Identifier(GeckoLib.ModID, "geo/creeper.geo.json");
	}

	@Override
	public Identifier getTextureLocation(Object object) {
		return new Identifier(GeckoLib.ModID, "textures/model/entity/creeper.png");
	}

	@Override
	public Identifier getAnimationFileLocation(Object animatable) {
		return new Identifier(GeckoLib.ModID, "animations/creeper.animation.json");
	}

}