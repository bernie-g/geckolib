package software.bernie.example.client.renderer.model.entity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class BatModel extends AnimatedGeoModel
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
