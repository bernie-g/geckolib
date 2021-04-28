package software.bernie.example.client.model.item;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PistolModel extends AnimatedGeoModel<PistolItem> {
	@Override
	public ResourceLocation getModelLocation(PistolItem object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/pistol.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(PistolItem object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/pistol.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PistolItem animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/pistol.animation.json");
	}
}
