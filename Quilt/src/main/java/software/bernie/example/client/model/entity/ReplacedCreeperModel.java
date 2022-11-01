package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class ReplacedCreeperModel extends AnimatedGeoModel {
	@Override
	public ResourceLocation getModelResource(Object object) {
		return EntityResources.CREEPER_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(Object object) {
		return EntityResources.CREEPER_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(Object animatable) {
		return EntityResources.CREEPER_ANIMATIONS;
	}
}