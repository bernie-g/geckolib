package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.CarEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CarModel extends AnimatedGeoModel<CarEntity> {
	@Override
	public ResourceLocation getAnimationResource(CarEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/race_car.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CarEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/race_car.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CarEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/entity/race_car.png");
	}
}