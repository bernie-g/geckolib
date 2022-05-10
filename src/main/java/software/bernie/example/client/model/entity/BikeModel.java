package software.bernie.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel<BikeEntity> {
	@Override
	public ResourceLocation getAnimationFileLocation(BikeEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/bike.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(BikeEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/bike.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(BikeEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/bike.png");
	}
}
