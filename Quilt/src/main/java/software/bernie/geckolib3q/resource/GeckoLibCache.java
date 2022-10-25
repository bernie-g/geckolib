package software.bernie.geckolib3q.resource;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.file.AnimationFile;
import software.bernie.geckolib3q.file.AnimationFileLoader;
import software.bernie.geckolib3q.file.GeoModelLoader;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.molang.MolangRegistrar;

public class GeckoLibCache {
	private Map<ResourceLocation, AnimationFile> animations = Collections.emptyMap();
	private Map<ResourceLocation, GeoModel> geoModels = Collections.emptyMap();
	private static GeckoLibCache INSTANCE;
	public final MolangParser parser = new MolangParser();
	private final AnimationFileLoader animationLoader;
	private final GeoModelLoader modelLoader;

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

	public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage,
			ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
			Executor backgroundExecutor, Executor gameExecutor) {
		Map<ResourceLocation, AnimationFile> animations = new Object2ObjectOpenHashMap<>();
		Map<ResourceLocation, GeoModel> geoModels = new Object2ObjectOpenHashMap<>();
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
				() -> resourceManager.listResources(type, fileName -> fileName.toString().endsWith(".json")), executor)
				.thenApplyAsync(resources -> {
					Map<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<>();

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
						// Skips moreplayermodels and customnpc namespaces as they use an animation
						// folder as well
						String namespace = entry.getKey().getNamespace();

						if (!namespace.equalsIgnoreCase("moreplayermodels") || !namespace.equalsIgnoreCase("customnpcs")
								|| !namespace.equalsIgnoreCase("gunsrpg")) {
							map.accept(entry.getKey(), entry.getValue().join());
						}
					}
				}, executor);
	}
}