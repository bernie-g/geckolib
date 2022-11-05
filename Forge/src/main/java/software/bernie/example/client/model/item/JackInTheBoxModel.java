package software.bernie.example.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JackInTheBoxModel extends AnimatedGeoModel<JackInTheBoxItem> {
	@Override
	public ResourceLocation getModelResource(JackInTheBoxItem object) {
		return new ResourceLocation(GeckoLib.MOD_ID, "geo/jack.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(JackInTheBoxItem object) {
		return new ResourceLocation(GeckoLib.MOD_ID, "textures/item/jack.png");
	}

	@Override
	public ResourceLocation getAnimationResource(JackInTheBoxItem animatable) {
		return new ResourceLocation(GeckoLib.MOD_ID, "animations/jackinthebox.animation.json");
	}
}
