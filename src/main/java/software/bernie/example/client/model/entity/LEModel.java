package software.bernie.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.entity.GeoExampleEntityLayer;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class LEModel extends AnimatedTickingGeoModel<GeoExampleEntityLayer> {

	@Override
	public ResourceLocation getModelLocation(GeoExampleEntityLayer object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/le.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(GeoExampleEntityLayer object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/le.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(GeoExampleEntityLayer animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/le.animations.json");
	}
}
