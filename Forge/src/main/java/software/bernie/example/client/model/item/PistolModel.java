package software.bernie.example.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PistolModel extends AnimatedGeoModel<PistolItem> {
	@Override
	public ResourceLocation getModelResource(PistolItem object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/pistol.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PistolItem object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/pistol.png");
	}

	@Override
	public ResourceLocation getAnimationResource(PistolItem animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/pistol.animation.json");
	}
}
