package software.bernie.geckolib.model.provider;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.provider.data.ExtraModelData;
import software.bernie.geckolib.resource.GeckoLibCache;

import java.util.HashMap;

public abstract class GeoModelProvider<T> implements IModelDataProvider
{
	public double seekTime;
	public double lastGameTickTime;
	public boolean shouldCrashOnMissing = false;
	private HashMap<Class<ExtraModelData>, ExtraModelData> extraModelData = new HashMap<>();

	@Override
	public HashMap<Class<ExtraModelData>, ExtraModelData> getAllModelData()
	{
		return extraModelData;
	}

	public GeoModel getModel(ResourceLocation location)
	{
		return GeckoLibCache.geoModels.get(location);
	}

	public abstract ResourceLocation getModelLocation(T object);
	public abstract ResourceLocation getTextureLocation(T object);
}
