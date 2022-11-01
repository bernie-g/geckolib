package software.bernie.example.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PistolModel extends AnimatedGeoModel<PistolItem> {
	@Override
	public ResourceLocation getModelLocation(PistolItem object) {
		return EntityResources.PISTOL_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(PistolItem object) {
		return EntityResources.PISTOL_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PistolItem animatable) {
		return EntityResources.PISTOL_ANIMATIONS;
	}
}
