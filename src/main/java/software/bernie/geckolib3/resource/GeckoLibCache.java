package software.bernie.geckolib3.resource;

import com.eliotlash.molang.MolangParser;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class GeckoLibCache {
	private static GeckoLibCache INSTANCE;

	private final AnimationFileLoader animationLoader;
	private final GeoModelLoader modelLoader;

	public final MolangParser parser = new MolangParser();

	public ConcurrentHashMap<ResourceLocation, AnimationFile> getAnimations() {
		if (!GeckoLib.hasInitialized) {
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
		}
		return animations;
	}

	public ConcurrentHashMap<ResourceLocation, GeoModel> getGeoModels() {
		if (!GeckoLib.hasInitialized) {
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
		}
		return geoModels;
	}

	private ConcurrentHashMap<ResourceLocation, AnimationFile> animations = new ConcurrentHashMap<>();
	private ConcurrentHashMap<ResourceLocation, GeoModel> geoModels = new ConcurrentHashMap<>();

	protected GeckoLibCache() {
		this.animationLoader = new AnimationFileLoader();
		this.modelLoader = new GeoModelLoader();
		MolangRegistrar.registerVars(parser);
	}

	public static GeckoLibCache getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GeckoLibCache();
			return INSTANCE;
		}
		return INSTANCE;
	}

	public CompletableFuture<Void> resourceReload(IFutureReloadListener.IStage stage, IResourceManager resourceManager,
			IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor,
			Executor gameExecutor) {
		ConcurrentHashMap<ResourceLocation, AnimationFile> tempAnimations = new ConcurrentHashMap<>();
		ConcurrentHashMap<ResourceLocation, GeoModel> tempModels = new ConcurrentHashMap<>();

		try {
			CompletableFuture[] animationFileFutures = resourceManager
					.getAllResourceLocations("animations", fileName -> fileName.endsWith(".json")).stream()
					.map(location -> CompletableFuture.supplyAsync(() -> location))
					.map(completable -> completable.thenAcceptAsync(resource -> tempAnimations.put(resource,
							animationLoader.loadAllAnimations(parser, resource, resourceManager))))
					.toArray(x -> new CompletableFuture[x]);

			CompletableFuture[] geoModelFutures = resourceManager
					.getAllResourceLocations("geo", fileName -> fileName.endsWith(".json")).stream()
					.map(location -> CompletableFuture.supplyAsync(() -> location))
					.map(completable -> completable.thenAcceptAsync(
							resource -> tempModels.put(resource, modelLoader.loadModel(resourceManager, resource)))
							.exceptionally((x) -> {
								GeckoLib.LOGGER.fatal(x);
								return null;
							}))
					.toArray(x -> new CompletableFuture[x]);
			CompletableFuture<Void> futures = CompletableFuture
					.allOf(ArrayUtils.addAll(animationFileFutures, geoModelFutures)).thenAccept(x -> {
						// Retain our behavior of completely replacing the old model map on reload
						ConcurrentHashMap<ResourceLocation, AnimationFile> hashAnim = new ConcurrentHashMap<>();
						hashAnim.putAll(tempAnimations);
						animations = hashAnim;

						ConcurrentHashMap<ResourceLocation, GeoModel> hashModel = new ConcurrentHashMap<>();
						hashModel.putAll(tempModels);
						geoModels = hashModel;
					}).thenCompose(stage::markCompleteAwaitingOthers);
			return futures;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
