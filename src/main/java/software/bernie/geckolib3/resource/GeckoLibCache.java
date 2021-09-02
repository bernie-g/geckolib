package software.bernie.geckolib3.resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.eliotlash.molang.MolangParser;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.file.AnimationFileLoader;
import software.bernie.geckolib3.file.GeoModelLoader;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.molang.MolangRegistrar;

public class GeckoLibCache {
	private static GeckoLibCache INSTANCE;

	private final AnimationFileLoader animationLoader;
	private final GeoModelLoader modelLoader;

	public final MolangParser parser = new MolangParser();

	public Map<ResourceLocation, AnimationFile> getAnimations() {
		if (!GeckoLib.hasInitialized) {
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
		}
		return animations;
	}

	public Map<ResourceLocation, GeoModel> getGeoModels() {
		if (!GeckoLib.hasInitialized) {
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
		}
		return geoModels;
	}

	private Map<ResourceLocation, AnimationFile> animations = Collections.emptyMap();
	private Map<ResourceLocation, GeoModel> geoModels = Collections.emptyMap();

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

	public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager,
			ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor,
			Executor gameExecutor) {
		Map<ResourceLocation, AnimationFile> animations = new HashMap<>();
		Map<ResourceLocation, GeoModel> geoModels = new HashMap<>();
		return CompletableFuture.allOf(loadResources(backgroundExecutor, resourceManager, "animations",
				animation -> animationLoader.loadAllAnimations(parser, animation, resourceManager), animations::put),
				loadResources(backgroundExecutor, resourceManager, "geo",
						resource -> modelLoader.loadModel(resourceManager, resource), geoModels::put))
				.thenCompose(stage::wait).thenAcceptAsync(empty -> {
					this.animations = animations;
					this.geoModels = geoModels;
				}, gameExecutor);
	}

	private static <T> CompletableFuture<Void> loadResources(Executor executor, ResourceManager resourceManager,
			String type, Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map) {
		return CompletableFuture.supplyAsync(
				() -> resourceManager.listResources(type, fileName -> fileName.endsWith(".json")), executor)
				.thenApplyAsync(resources -> {
					Map<ResourceLocation, CompletableFuture<T>> tasks = new HashMap<>();

					for (ResourceLocation resource : resources) {
						CompletableFuture<T> existing = tasks.put(resource,
								CompletableFuture.supplyAsync(() -> loader.apply(resource), executor));

						if (existing != null) {// Possibly if this matters, the last one will win
							System.err.println("Duplicate resource for " + resource);
							existing.cancel(false);
						}
					}

					return tasks;
				}, executor).thenAcceptAsync(tasks -> {
					for (Entry<ResourceLocation, CompletableFuture<T>> entry : tasks.entrySet()) {
						// Shouldn't be any duplicates as they are caught above
						map.accept(entry.getKey(), entry.getValue().join());
					}
				}, executor);
	}
}
