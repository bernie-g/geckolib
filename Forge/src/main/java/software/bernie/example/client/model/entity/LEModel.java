package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class LEModel extends AnimatedTickingGeoModel<LEEntity> {

	@Override
	public ResourceLocation getModelResource(LEEntity object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/le.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LEEntity object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/entity/le.png");
	}

	@Override
	public ResourceLocation getAnimationResource(LEEntity animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/le.animations.json");
	}

}