package software.bernie.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class ExampleEntityModel extends AnimatedGeoModel
{
	@Override
	public ResourceLocation getAnimationFileLocation(Object entity)
	{
		return new ResourceLocation(GeckoLib.ModID, "animations/ice_bee.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(Object entity)
	{
		return new ResourceLocation(GeckoLib.ModID, "geo/ice_bee.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Object entity)
	{
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/ice_bee_tex.png");
	}
}
