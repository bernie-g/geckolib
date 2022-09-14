package software.bernie.example.client.model.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PotatoArmorModel extends AnimatedGeoModel<PotatoArmorItem> {
	@Override
	public ResourceLocation getModelResource(PotatoArmorItem object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/potato_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PotatoArmorItem object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/potato_armor.png");
	}

	@Override
	public ResourceLocation getAnimationResource(PotatoArmorItem animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/potato_armor.animation.json");
	}
}
