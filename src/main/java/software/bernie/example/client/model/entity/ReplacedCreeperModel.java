package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReplacedCreeperModel extends AnimatedGeoModel {
	@Override
	public ResourceLocation getModelResource(Object object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/creeper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Object object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/creeper.png");
	}

	@Override
	public ResourceLocation getAnimationResource(Object animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/creeper.animation.json");
	}
}
