package software.bernie.example.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class PistolModel extends AnimatedGeoModel<PistolItem> {
	@Override
	public ResourceLocation getModelResource(PistolItem object) {
		return EntityResources.PISTOL_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(PistolItem object) {
		return EntityResources.PISTOL_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(PistolItem animatable) {
		return EntityResources.PISTOL_ANIMATIONS;
	}
}
