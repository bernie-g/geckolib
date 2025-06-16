package anightdazingzoroark.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.example.entity.BikeEntity;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel<BikeEntity> {
	@Override
	public ResourceLocation getAnimationFileLocation(BikeEntity entity) {
		return new ResourceLocation(RiftLib.ModID, "animations/bike.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(BikeEntity entity) {
		return new ResourceLocation(RiftLib.ModID, "geo/bike.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(BikeEntity entity) {
		return new ResourceLocation(RiftLib.ModID, "textures/model/entity/bike.png");
	}
}
