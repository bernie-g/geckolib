package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.entity.CarEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CarModel extends AnimatedGeoModel<CarEntity> {
	@Override
	public Identifier getAnimationFileLocation(CarEntity entity) {
		return new Identifier(GeckoLib.ModID, "animations/race_car.animation.json");
	}

	@Override
	public Identifier getModelLocation(CarEntity entity) {
		return new Identifier(GeckoLib.ModID, "geo/race_car.geo.json");
	}

	@Override
	public Identifier getTextureLocation(CarEntity entity) {
		return new Identifier(GeckoLib.ModID, "textures/entity/race_car.png");
	}
}