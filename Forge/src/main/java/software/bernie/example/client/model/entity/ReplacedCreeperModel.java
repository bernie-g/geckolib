package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReplacedCreeperModel extends AnimatedGeoModel {
	@Override
	public ResourceLocation getModelLocation(Object object) {
		return EntityResources.CREEPER_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(Object object) {
		return EntityResources.CREEPER_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Object animatable) {
		return EntityResources.CREEPER_ANIMATIONS;
	}
}