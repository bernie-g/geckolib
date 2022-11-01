package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.client.EntityResources;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class LEModel extends AnimatedTickingGeoModel<LEEntity> {

	@Override
	public Identifier getModelLocation(LEEntity object) {
		return EntityResources.LAYER_EXAMPLE_MODEL;
	}

	@Override
	public Identifier getTextureLocation(LEEntity object) {
		return EntityResources.LAYER_EXAMPLE_TEXTURE;
	}

	@Override
	public Identifier getAnimationFileLocation(LEEntity animatable) {
		return EntityResources.LAYER_EXAMPLE_ANIMATIONS;
	}

}