package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class LEModel extends AnimatedTickingGeoModel<LEEntity> {

	@Override
	public ResourceLocation getModelLocation(LEEntity object) {
		return EntityResources.LAYER_EXAMPLE_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(LEEntity object) {
		return EntityResources.LAYER_EXAMPLE_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(LEEntity animatable) {
		return EntityResources.LAYER_EXAMPLE_ANIMATIONS;
	}

}