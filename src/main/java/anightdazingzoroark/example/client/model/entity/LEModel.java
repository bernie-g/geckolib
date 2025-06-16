package anightdazingzoroark.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.example.entity.GeoExampleEntityLayer;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedTickingGeoModel;

public class LEModel extends AnimatedTickingGeoModel<GeoExampleEntityLayer> {

	@Override
	public ResourceLocation getModelLocation(GeoExampleEntityLayer object) {
		return new ResourceLocation(RiftLib.ModID, "geo/le.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(GeoExampleEntityLayer object) {
		return new ResourceLocation(RiftLib.ModID, "textures/model/entity/le.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(GeoExampleEntityLayer animatable) {
		return new ResourceLocation(RiftLib.ModID, "animations/le.animations.json");
	}
}
