package software.bernie.geckolib.example.client.renderer.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.entity.GeoExampleEntity;
import software.bernie.geckolib.model.AnimatedGeoModel;

public class ExampleGeoModel extends AnimatedGeoModel<GeoExampleEntity>
{
	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return new ResourceLocation(GeckoLib.ModID, "animations/botarium_tier1_anim.json");
	}

	@Override
	public ResourceLocation getModelLocation()
	{
		return new ResourceLocation(GeckoLib.ModID, "geo/geotestmodel.json");
	}
}
