package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel<BikeEntity> {
	@Override
	public Identifier getAnimationFileLocation(BikeEntity entity) {
		return new Identifier(GeckoLib.ModID, "animations/bike.animation.json");
	}

	@Override
	public Identifier getModelLocation(BikeEntity entity) {
		return new Identifier(GeckoLib.ModID, "geo/bike.geo.json");
	}

	@Override
	public Identifier getTextureLocation(BikeEntity entity) {
		return new Identifier(GeckoLib.ModID, "textures/model/entity/bike.png");
	}
}