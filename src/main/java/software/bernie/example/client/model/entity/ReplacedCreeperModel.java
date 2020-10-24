package software.bernie.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class ReplacedCreeperModel extends AnimatedGeoModel
{
	@Override
	public ResourceLocation getModelLocation(Object object)
	{
		return new ResourceLocation(GeckoLib.ModID, "geo/creeper.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Object object)
	{
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/creeper.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Object animatable)
	{
		return new ResourceLocation(GeckoLib.ModID, "animations/creeper.animation.json");
	}
}
