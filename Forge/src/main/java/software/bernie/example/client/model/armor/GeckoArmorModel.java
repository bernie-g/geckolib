package software.bernie.example.client.model.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GeckoArmorModel extends AnimatedGeoModel<GeckoArmorItem> {
	@Override
	public ResourceLocation getModelLocation(GeckoArmorItem object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/geckoarmor.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(GeckoArmorItem object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/geckoarmor_armor.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(GeckoArmorItem animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/geckoarmor.animation.json");
	}
}
