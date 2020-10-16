package software.bernie.geckolib.model.provider;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.resource.GeckoLibCache;

public abstract class GeoModelProvider<T>
{
	public double seekTime;
	public double lastGameTickTime;
	public boolean shouldCrashOnMissing = false;

	public GeoModel getModel(Identifier location)
	{
		return GeckoLibCache.geoModels.get(location);
	}

	public abstract Identifier getModelLocation(T object);
	public abstract Identifier getTextureLocation(T object);
}