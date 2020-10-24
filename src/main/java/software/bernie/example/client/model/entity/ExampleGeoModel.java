package software.bernie.example.client.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class ExampleGeoModel extends AnimatedGeoModel<GeoExampleEntity>
{
	@Override
	public ResourceLocation getAnimationFileLocation(GeoExampleEntity entity)
	{
		return new ResourceLocation(GeckoLib.ModID, "animations/botarium.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(GeoExampleEntity entity)
	{
		return new ResourceLocation(GeckoLib.ModID, "geo/geotestmodel.json");
	}

	@Override
	public ResourceLocation getTextureLocation(GeoExampleEntity entity)
	{
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/botarium.png");
	}
}
