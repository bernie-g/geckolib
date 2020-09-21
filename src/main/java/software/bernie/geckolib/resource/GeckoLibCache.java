package software.bernie.geckolib.resource;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import software.bernie.geckolib.file.AnimationFile;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.file.GeoModelLoader;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.molang.MolangRegistrar;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class GeckoLibCache
{
	private static GeckoLibCache INSTANCE;

	private final AnimationFileLoader animationLoader;
	private final GeoModelLoader modelLoader;

	public final MolangParser parser = new MolangParser();

	public static HashMap<ResourceLocation, AnimationFile> animations = new HashMap<>();
	public static HashMap<ResourceLocation, GeoModel> geoModels = new HashMap<>();

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

	public CompletableFuture<Void> resourceReload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
	{
		animations.clear();
		geoModels.clear();
		CompletableFuture[] animationFileFutures = resourceManager.getAllResourceLocations("animations", fileName -> fileName.endsWith(".json"))
				.stream()
				.map(location -> CompletableFuture.supplyAsync(() -> location))
				.map(completable -> completable.thenAcceptAsync(resource -> animations.put(resource, animationLoader.loadAllAnimations(parser, resource, resourceManager))))
				.toArray(x -> new CompletableFuture[x]);

		CompletableFuture[] geoModelFutures = resourceManager.getAllResourceLocations("geo", fileName -> fileName.endsWith(".json"))
				.stream()
				.map(location -> CompletableFuture.supplyAsync(() -> location))
				.map(completable -> completable.thenAcceptAsync(resource -> geoModels.put(resource, modelLoader.loadModel(resourceManager, resource))))
				.toArray(x -> new CompletableFuture[x]);

		return CompletableFuture.allOf(ArrayUtils.addAll(animationFileFutures, geoModelFutures)).thenCompose(stage::markCompleteAwaitingOthers);
	}
}
