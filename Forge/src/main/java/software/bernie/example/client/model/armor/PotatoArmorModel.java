package software.bernie.example.client.model.armor;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PotatoArmorModel extends AnimatedGeoModel<PotatoArmorItem> {
	@Override
	public ResourceLocation getModelLocation(PotatoArmorItem object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/potato_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(PotatoArmorItem object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/potato_armor.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PotatoArmorItem animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/potato_armor.animation.json");
	}
}
