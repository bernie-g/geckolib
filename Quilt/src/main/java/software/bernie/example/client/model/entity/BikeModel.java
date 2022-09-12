package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel<BikeEntity> {
	@Override
	public Identifier getAnimationResource(BikeEntity entity) {
		return new Identifier(GeckoLib.ModID, "animations/bike.animation.json");
	}

	@Override
	public Identifier getModelResource(BikeEntity entity) {
		return new Identifier(GeckoLib.ModID, "geo/bike.geo.json");
	}

	@Override
	public Identifier getTextureResource(BikeEntity entity) {
		return new Identifier(GeckoLib.ModID, "textures/model/entity/bike.png");
	}
}