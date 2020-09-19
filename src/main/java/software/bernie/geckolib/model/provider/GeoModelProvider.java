package software.bernie.geckolib.model.provider;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.decorator.ExtraModelData;
import software.bernie.geckolib.decorator.IModelDataProvider;
import software.bernie.geckolib.file.GeoModelLoader;
import software.bernie.geckolib.geo.render.built.GeoModel;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public abstract class GeoModelProvider<T> implements IModelDataProvider
{

	public double seekTime;
	public double lastGameTickTime;
	public boolean shouldCrashOnMissing = false;
	public final GeoModelLoader modelLoader;
	public IGenericModelProvider genericModelProvider;
	private HashMap<Class<ExtraModelData>, ExtraModelData> extraModelData = new HashMap<>();

	protected GeoModelProvider()
	{
		modelLoader = new GeoModelLoader(this);
	}

	@Override
	public HashMap<Class<ExtraModelData>, ExtraModelData> getAllModelData()
	{
		return extraModelData;
	}

	protected final LoadingCache<ResourceLocation, GeoModel> modelCache = CacheBuilder.newBuilder().build(new CacheLoader<ResourceLocation, GeoModel>()
	{
		@Override
		public GeoModel load(ResourceLocation key)
		{
			GeoModel geoModel = modelLoader.loadModel(Minecraft.getInstance().getResourceManager(), key);
			genericModelProvider.reloadModel(geoModel);
			return geoModel;
		}
	});

	public GeoModel getModel(ResourceLocation location)
	{
		try
		{
			return this.modelCache.get(location);
		}
		catch (ExecutionException e)
		{
			throw new RuntimeException(e);
		}
	}

	public abstract ResourceLocation getModelLocation(T animatable);
}
