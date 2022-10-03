package software.bernie.example.client.model.armor;

import net.minecraft.util.Identifier;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GeckoArmorModel extends AnimatedGeoModel<GeckoArmorItem> {
	@Override
	public Identifier getModelLocation(GeckoArmorItem object) {
		return new Identifier(GeckoLib.ModID, "geo/geckoarmor.geo.json");
	}

	@Override
	public Identifier getTextureLocation(GeckoArmorItem object) {
		return new Identifier(GeckoLib.ModID, "textures/item/geckoarmor_armor.png");
	}

	@Override
	public Identifier getAnimationFileLocation(GeckoArmorItem animatable) {
		return new Identifier(GeckoLib.ModID, "animations/geckoarmor.animation.json");
	}
}
