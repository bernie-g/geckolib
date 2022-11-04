package software.bernie.geckolib3.resource;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.file.BakedAnimations;
import software.bernie.geckolib3.file.AnimationFileLoader;
import software.bernie.geckolib3.file.GeoModelLoader;
import software.bernie.geckolib3.geo.render.built.BakedGeoModel;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GeckoLibCache {
	private static final Set<String> excludedNamespaces = ObjectOpenHashSet.of("moreplayermodels", "customnpcs", "gunsrpg");

	private static final AnimationFileLoader animationLoader = new AnimationFileLoader();
	private static final GeoModelLoader modelLoader = new GeoModelLoader();

	private static Map<ResourceLocation, BakedAnimations> animations = Collections.emptyMap();
	private static Map<ResourceLocation, BakedGeoModel> geoModels = Collections.emptyMap();

	private GeckoLibCache() {}

	public static Map<ResourceLocation, BakedAnimations> getBakedAnimations() {
		if (!GeckoLib.hasInitialized)
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");

		return animations;
	}

	public static Map<ResourceLocation, BakedGeoModel> getBakedModels() {
		if (!GeckoLib.hasInitialized)
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");

		return geoModels;
	}

	public static CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager,
			ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor,
			Executor gameExecutor) {
		Map<ResourceLocation, BakedAnimations> animations = new Object2ObjectOpenHashMap<>();
		Map<ResourceLocation, BakedGeoModel> geoModels = new Object2ObjectOpenHashMap<>();

		return CompletableFuture.allOf(loadResources(backgroundExecutor, resourceManager, "animations",
				animation -> animationLoader.loadAllAnimations(animation, resourceManager), animations::put),
				loadResources(backgroundExecutor, resourceManager, "geo",
						resource -> modelLoader.loadModel(resourceManager, resource), geoModels::put))
				.thenCompose(stage::wait).thenAcceptAsync(empty -> {
					GeckoLibCache.animations = animations;
					GeckoLibCache.geoModels = geoModels;
				}, gameExecutor);
	}

	private static <T> CompletableFuture<Void> loadResources(Executor executor, ResourceManager resourceManager,
			String type, Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map) {
		return CompletableFuture.supplyAsync(
				() -> resourceManager.listResources(type, fileName -> fileName.toString().endsWith(".json")), executor)
				.thenApplyAsync(resources -> {
					Map<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<>();

					for (ResourceLocation resource : resources.keySet()) {
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
						// Skip known namespaces that use an "animation" folder as well
						if (!excludedNamespaces.contains(entry.getKey().getNamespace().toLowerCase(Locale.ROOT)))
							map.accept(entry.getKey(), entry.getValue().join());
					}
				}, executor);
	}

	public static void registerReloadListener() {
		if (Minecraft.getInstance() == null) {
			if (!ModLoader.isDataGenRunning())
				GeckoLib.LOGGER.warn("Minecraft.getInstance() was null, could not register reload listeners. Ignore if datagenning.");

			return;
		}

		if (Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager resourceManager) {
			resourceManager.registerReloadListener(GeckoLibCache::reload);
		}
		else {
			throw new RuntimeException("GeckoLib was initialized too early!");
		}
	}
}
