package software.bernie.example.client.renderer.model.armor;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class PotatoArmorModel extends AnimatedGeoModel<PotatoArmorItem>
{
	@Override
	public ResourceLocation getModelLocation(PotatoArmorItem object)
	{
		return new ResourceLocation(GeckoLib.ModID, "geo/wolf_armor.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(PotatoArmorItem object)
	{
		return new ResourceLocation(GeckoLib.ModID, "textures/item/wolf_armor.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(PotatoArmorItem animatable)
	{
		return new ResourceLocation(GeckoLib.ModID, "animations/potato_armor.animation.json");
	}
}
