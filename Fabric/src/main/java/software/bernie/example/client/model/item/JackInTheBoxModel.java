package software.bernie.example.client.model.item;

import net.minecraft.util.Identifier;
import software.bernie.example.client.EntityResources;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JackInTheBoxModel extends AnimatedGeoModel<JackInTheBoxItem> {
	@Override
	public Identifier getModelLocation(JackInTheBoxItem object) {
		return EntityResources.JACKINTHEBOX_MODEL;
	}

	@Override
	public Identifier getTextureLocation(JackInTheBoxItem object) {
		return EntityResources.JACKINTHEBOX_TEXTURE;
	}

	@Override
	public Identifier getAnimationFileLocation(JackInTheBoxItem animatable) {
		return EntityResources.JACKINTHEBOX_ANIMATIONS;	
	}
}
