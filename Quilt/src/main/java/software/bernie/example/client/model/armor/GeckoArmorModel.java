package software.bernie.example.client.model.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.client.EntityResources;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class GeckoArmorModel extends AnimatedGeoModel<GeckoArmorItem> {
	@Override
	public ResourceLocation getModelResource(GeckoArmorItem object) {
		return EntityResources.GECKOARMOR_MODEL;
	}

	@Override
	public ResourceLocation getTextureResource(GeckoArmorItem object) {
		return EntityResources.GECKOARMOR_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(GeckoArmorItem animatable) {
		return EntityResources.GECKOARMOR_ANIMATIONS;
	}
}
