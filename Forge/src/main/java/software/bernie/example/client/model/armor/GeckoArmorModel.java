package software.bernie.example.client.model.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GeckoArmorModel extends AnimatedGeoModel<GeckoArmorItem> {
	@Override
	public ResourceLocation getModelLocation(GeckoArmorItem object) {
		return EntityResources.GECKOARMOR_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(GeckoArmorItem object) {
		return EntityResources.GECKOARMOR_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(GeckoArmorItem animatable) {
		return EntityResources.GECKOARMOR_ANIMATIONS;
	}
}
