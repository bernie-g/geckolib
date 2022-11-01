package software.bernie.example.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class JackInTheBoxModel extends AnimatedGeoModel<JackInTheBoxItem> {
	@Override
	public ResourceLocation getModelResource(JackInTheBoxItem object) {
		return EntityResources.JACKINTHEBOX_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(JackInTheBoxItem object) {
		return EntityResources.JACKINTHEBOX_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(JackInTheBoxItem animatable) {
		return EntityResources.JACKINTHEBOX_ANIMATIONS;	
	}
}
