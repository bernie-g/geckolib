package software.bernie.geckolib3.cache;

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
import software.bernie.geckolib3.GeckoLibException;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.loading.FileLoader;
import software.bernie.geckolib3.loading.json.FormatVersion;
import software.bernie.geckolib3.loading.json.raw.Model;
import software.bernie.geckolib3.loading.object.BakedAnimations;
import software.bernie.geckolib3.loading.object.BakedModelFactory;
import software.bernie.geckolib3.loading.object.GeometryTree;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Cache class for holding loaded {@link software.bernie.geckolib3.core.animation.Animation Animations}
 * and {@link software.bernie.geckolib3.core.animatable.model.GeoModel Models}
 */
public final class GeckoLibCache {
	private static final Set<String> EXCLUDED_NAMESPACES = ObjectOpenHashSet.of("moreplayermodels", "customnpcs", "gunsrpg");

	private static Map<ResourceLocation, BakedAnimations> ANIMATIONS = Collections.emptyMap();
	private static Map<ResourceLocation, BakedGeoModel> MODELS = Collections.emptyMap();

	public static Map<ResourceLocation, BakedAnimations> getBakedAnimations() {
		if (!GeckoLib.hasInitialized)
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");

		return ANIMATIONS;
	}

	public static Map<ResourceLocation, BakedGeoModel> getBakedModels() {
		if (!GeckoLib.hasInitialized)
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");

		return MODELS;
	}

	public static void registerReloadListener() {
		if (Minecraft.getInstance() == null) {
			if (!ModLoader.isDataGenRunning())
				GeckoLib.LOGGER.warn("Minecraft.getInstance() was null, could not register reload listeners");

			return;
		}

		if (!(Minecraft.getInstance().getResourceManager() instanceof ReloadableResourceManager resourceManager))
			throw new RuntimeException("GeckoLib was initialized too early!");

		resourceManager.registerReloadListener(GeckoLibCache::reload);
	}

	private static CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager,
			ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor,
			Executor gameExecutor) {
		Map<ResourceLocation, BakedAnimations> animations = new Object2ObjectOpenHashMap<>();
		Map<ResourceLocation, BakedGeoModel> models = new Object2ObjectOpenHashMap<>();

		return CompletableFuture.allOf(
				loadAnimations(backgroundExecutor, resourceManager, animations::put),
				loadModels(backgroundExecutor, resourceManager, models::put))
				.thenCompose(stage::wait).thenAcceptAsync(empty -> {
					GeckoLibCache.ANIMATIONS = animations;
					GeckoLibCache.MODELS = models;
				}, gameExecutor);
	}

	private static CompletableFuture<Void> loadAnimations(Executor backgroundExecutor, ResourceManager resourceManager, BiConsumer<ResourceLocation, BakedAnimations> elementConsumer) {
		return loadResources(backgroundExecutor, resourceManager, "animations", resource ->
				FileLoader.loadAnimationsFile(resource, resourceManager), elementConsumer);
	}

	private static CompletableFuture<Void> loadModels(Executor backgroundExecutor, ResourceManager resourceManager, BiConsumer<ResourceLocation, BakedGeoModel> elementConsumer) {
		return loadResources(backgroundExecutor, resourceManager, "geo", resource -> {
			Model model = FileLoader.loadModelFile(resource, resourceManager);

			if (model.formatVersion() != FormatVersion.V_1_12_0)
				throw new GeckoLibException(resource, "Unsupported geometry json version. Supported versions: 1.12.0");

			return BakedModelFactory.getForNamespace(resource.getNamespace()).constructGeoModel(GeometryTree.fromModel(model));
			}, elementConsumer);
	}

	private static <T> CompletableFuture<Void> loadResources(Executor executor, ResourceManager resourceManager,
			String type, Function<ResourceLocation, T> loader, BiConsumer<ResourceLocation, T> map) {
		return CompletableFuture.supplyAsync(
				() -> resourceManager.listResources(type, fileName -> fileName.toString().endsWith(".json")), executor)
				.thenApplyAsync(resources -> {
					Map<ResourceLocation, CompletableFuture<T>> tasks = new Object2ObjectOpenHashMap<>();

					for (ResourceLocation resource : resources.keySet()) {
						tasks.put(resource, CompletableFuture.supplyAsync(() -> loader.apply(resource), executor));
					}

					return tasks;
				}, executor)
				.thenAcceptAsync(tasks -> {
					for (Entry<ResourceLocation, CompletableFuture<T>> entry : tasks.entrySet()) {
						// Skip known namespaces that use an "animation" folder as well
						if (!EXCLUDED_NAMESPACES.contains(entry.getKey().getNamespace().toLowerCase(Locale.ROOT)))
							map.accept(entry.getKey(), entry.getValue().join());
					}
				}, executor);
	}
}
