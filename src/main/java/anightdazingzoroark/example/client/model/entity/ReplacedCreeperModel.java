package anightdazingzoroark.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;

@SuppressWarnings("rawtypes")
public class ReplacedCreeperModel extends AnimatedGeoModel {
	@Override
	public ResourceLocation getModelLocation(Object object) {
		return new ResourceLocation(RiftLib.ModID, "geo/creeper.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Object object) {
		return new ResourceLocation(RiftLib.ModID, "textures/model/entity/creeper.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Object animatable) {
		return new ResourceLocation(RiftLib.ModID, "animations/creeper.animation.json");
	}
}
