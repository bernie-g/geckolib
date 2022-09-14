package software.bernie.example.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class JackInTheBoxModel extends AnimatedGeoModel<JackInTheBoxItem> {
	@Override
	public ResourceLocation getModelResource(JackInTheBoxItem object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/jack.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(JackInTheBoxItem object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/jack.png");
	}

	@Override
	public ResourceLocation getAnimationResource(JackInTheBoxItem animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/jackinthebox.animation.json");
	}
}
