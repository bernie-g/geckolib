package software.bernie.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel {
	@Override
	public ResourceLocation getAnimationFileLocation(Object entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/bike.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(Object entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/bike.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Object entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/bike.png");
	}
}
