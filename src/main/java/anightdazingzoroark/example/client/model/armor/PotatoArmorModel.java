package anightdazingzoroark.example.client.model.armor;

import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.example.item.PotatoArmorItem;
import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.model.AnimatedGeoModel;

public class PotatoArmorModel extends AnimatedGeoModel<PotatoArmorItem> {
	@Override
	public ResourceLocation getModelLocation(PotatoArmorItem object) {
		return new ResourceLocation(RiftLib.ModID, "geo/potato_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(PotatoArmorItem object) {
		return new ResourceLocation(RiftLib.ModID, "textures/item/potato_armor.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PotatoArmorItem animatable) {
		return new ResourceLocation(RiftLib.ModID, "animations/potato_armor.animation.json");
	}
}
