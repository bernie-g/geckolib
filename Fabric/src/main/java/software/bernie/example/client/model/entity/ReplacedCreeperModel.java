package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.example.client.EntityResources;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReplacedCreeperModel extends AnimatedGeoModel {
	@Override
	public Identifier getModelLocation(Object object) {
		return EntityResources.CREEPER_MODEL;
	}

	@Override
	public Identifier getTextureLocation(Object object) {
		return EntityResources.CREEPER_TEXTURE;
	}

	@Override
	public Identifier getAnimationFileLocation(Object animatable) {
		return EntityResources.CREEPER_ANIMATIONS;
	}
}