package software.bernie.example.client.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ExampleEntityModel extends AnimatedGeoModel
{
	@Override
	public Identifier getAnimationFileLocation(Object entity)
	{
		return new Identifier(GeckoLib.ModID, "animations/bat.animation.json");
	}

	@Override
	public Identifier getModelLocation(Object entity)
	{
		return new Identifier(GeckoLib.ModID, "geo/bat.geo.json");
	}

	@Override
	public Identifier getTextureLocation(Object entity)
	{
		return new Identifier(GeckoLib.ModID, "textures/model/entity/bat.png");
	}
}
