package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BikeModel extends AnimatedGeoModel
{
	@Override
	public Identifier getAnimationFileLocation(Object entity)
	{
		return new Identifier(GeckoLib.ModID, "animations/bike.animation.json");
	}

	@Override
	public Identifier getModelLocation(Object entity)
	{
		return new Identifier(GeckoLib.ModID, "geo/bike.geo.json");
	}

	@Override
	public Identifier getTextureLocation(Object entity)
	{
		return new Identifier(GeckoLib.ModID, "textures/model/entity/bike.png");
	}
}