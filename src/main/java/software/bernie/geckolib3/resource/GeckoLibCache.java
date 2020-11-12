package software.bernie.geckolib3.resource;

import com.eliotlash.molang.MolangParser;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.file.AnimationFileLoader;
import software.bernie.geckolib3.file.GeoModelLoader;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.molang.MolangRegistrar;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class GeckoLibCache implements IResourceManagerReloadListener
{
	private static GeckoLibCache INSTANCE;

	private final AnimationFileLoader animationLoader;
	private final GeoModelLoader modelLoader;

	public final MolangParser parser = new MolangParser();

	public HashMap<ResourceLocation, AnimationFile> getAnimations()
	{
		if(!GeckoLib.hasInitialized)
		{
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
		}
		return animations;
	}

	public HashMap<ResourceLocation, GeoModel> getGeoModels()
	{
		if(!GeckoLib.hasInitialized)
		{
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
		}
		return geoModels;
	}

	private HashMap<ResourceLocation, AnimationFile> animations = new HashMap<>();
	private HashMap<ResourceLocation, GeoModel> geoModels = new HashMap<>();

	protected GeckoLibCache()
	{
		this.animationLoader = new AnimationFileLoader();
		this.modelLoader = new GeoModelLoader();
		MolangRegistrar.registerVars(parser);
	}


	public static GeckoLibCache getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new GeckoLibCache();
			return INSTANCE;
		}
		return INSTANCE;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		// TODO: ...
	}

	public CompletableFuture<Void> resourceReload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
	{
		HashMap<ResourceLocation, AnimationFile> tempAnimations = new HashMap<>();
		HashMap<ResourceLocation, GeoModel> tempModels = new HashMap<>();

		try
		{
			CompletableFuture[] animationFileFutures = resourceManager.getAllResourceLocations("animations", fileName -> fileName.endsWith(".json"))
					.stream()
					.map(location -> CompletableFuture.supplyAsync(() -> location))
					.map(completable -> completable.thenAcceptAsync(resource -> tempAnimations.put(resource, animationLoader.loadAllAnimations(parser, resource, resourceManager))))
					.toArray(x -> new CompletableFuture[x]);

			CompletableFuture[] geoModelFutures = resourceManager.getAllResourceLocations("geo", fileName -> fileName.endsWith(".json"))
					.stream()
					.map(location -> CompletableFuture.supplyAsync(() -> location))
					.map(completable -> completable.thenAcceptAsync(resource -> tempModels.put(resource, modelLoader.loadModel(resourceManager, resource))).exceptionally((x) ->
					{
						GeckoLib.LOGGER.fatal(x);
						return null;
					}))
					.toArray(x -> new CompletableFuture[x]);
			CompletableFuture<Void> futures = CompletableFuture.allOf(ArrayUtils.addAll(animationFileFutures, geoModelFutures)).thenAccept(x ->
			{
				animations = tempAnimations;
				geoModels = tempModels;
			}).thenCompose(stage::markCompleteAwaitingOthers);
			return futures;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
