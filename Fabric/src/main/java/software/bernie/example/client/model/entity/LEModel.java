package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class LEModel extends AnimatedTickingGeoModel<LEEntity> {

	@Override
	public Identifier getModelLocation(LEEntity object) {
		return new Identifier(GeckoLib.ModID, "geo/le.geo.json");
	}

	@Override
	public Identifier getTextureLocation(LEEntity object) {
		return new Identifier(GeckoLib.ModID, "textures/entity/le.png");
	}

	@Override
	public Identifier getAnimationFileLocation(LEEntity animatable) {
		return new Identifier(GeckoLib.ModID, "animations/le.animations.json");
	}

}