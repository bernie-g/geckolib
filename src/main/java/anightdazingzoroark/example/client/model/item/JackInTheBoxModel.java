package anightdazingzoroark.example.client.model.item;

import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.example.item.JackInTheBoxItem;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;

public class JackInTheBoxModel extends AnimatedGeoModel<JackInTheBoxItem> {
	@Override
	public ResourceLocation getModelLocation(JackInTheBoxItem object) {
		return new ResourceLocation(RiftLib.ModID, "geo/jack.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(JackInTheBoxItem object) {
		return new ResourceLocation(RiftLib.ModID, "textures/item/jack.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(JackInTheBoxItem animatable) {
		return new ResourceLocation(RiftLib.ModID, "animations/jackinthebox.animation.json");
	}
}
