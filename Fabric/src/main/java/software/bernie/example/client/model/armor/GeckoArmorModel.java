package software.bernie.example.client.model.armor;

import net.minecraft.util.Identifier;
import software.bernie.example.client.EntityResources;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GeckoArmorModel extends AnimatedGeoModel<GeckoArmorItem> {
	@Override
	public Identifier getModelLocation(GeckoArmorItem object) {
		return EntityResources.GECKOARMOR_MODEL;
	}

	@Override
	public Identifier getTextureLocation(GeckoArmorItem object) {
		return EntityResources.GECKOARMOR_TEXTURE;
	}

	@Override
	public Identifier getAnimationFileLocation(GeckoArmorItem animatable) {
		return EntityResources.GECKOARMOR_ANIMATIONS;
	}
}
