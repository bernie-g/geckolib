package software.bernie.geckolib.cache;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.json.ModelFormatVersion;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.object.CompoundException;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cache class for holding loaded {@link Animation Animations}
 * and {@link GeoModel Models}
 */
public final class GeckoLibResources {
	public static final ResourceLocation RELOAD_LISTENER_ID = GeckoLibConstants.id("geckolib_resources");
	public static final ResourceLocation ANIMATIONS_PATH = GeckoLibConstants.id("geckolib/animations");
	public static final ResourceLocation MODELS_PATH = GeckoLibConstants.id("geckolib/models");

	private static Map<ResourceLocation, BakedAnimations> ANIMATIONS = Collections.emptyMap();
	private static Map<ResourceLocation, BakedGeoModel> MODELS = Collections.emptyMap();

	/**
	 * Get GeckoLib's cache of all the loaded animations from the {@link #ANIMATIONS_PATH}
	 */
	public static Map<ResourceLocation, BakedAnimations> getBakedAnimations() {
		return ANIMATIONS;
	}

	/**
	 * Get GeckoLib's cache of all the loaded geo models from the {@link #MODELS_PATH}
	 */
	public static Map<ResourceLocation, BakedGeoModel> getBakedModels() {
		return MODELS;
	}
	// TODO CHECK ERROR VISIBILITY
	@ApiStatus.Internal
	public static CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
		CompletableFuture<Map<ResourceLocation, BakedAnimations>> animations = loadAnimations(backgroundExecutor, resourceManager);
		CompletableFuture<Map<ResourceLocation, BakedGeoModel>> models = loadModels(backgroundExecutor, resourceManager);

		return CompletableFuture.allOf(animations, models).thenCompose(stage::wait).thenRunAsync(() -> {
			GeckoLibResources.ANIMATIONS = animations.join();
			GeckoLibResources.MODELS = models.join();
		}, gameExecutor);
	}

	/**
	 * Provide a {@link Future} for retrieving and baking all animation jsons from the {@link #ANIMATIONS_PATH}
	 */
	private static CompletableFuture<Map<ResourceLocation, BakedAnimations>> loadAnimations(Executor backgroundExecutor, ResourceManager resourceManager) {
		return bakeJsonResources(backgroundExecutor, resourceManager, ANIMATIONS_PATH.getPath(), GeckoLibResources::bakeAnimations,
								 ex -> new BakedAnimations(new Object2ObjectOpenHashMap<>()));
	}

	/**
	 * Provide a {@link Future} for retrieving and baking all geo model jsons from the {@link #MODELS_PATH}
	 */
	private static CompletableFuture<Map<ResourceLocation, BakedGeoModel>> loadModels(Executor backgroundExecutor, ResourceManager resourceManager) {
		return bakeJsonResources(backgroundExecutor, resourceManager, MODELS_PATH.getPath(), GeckoLibResources::bakeModel,
								 ex -> null);
	}

	/**
	 * Retrieve all asset json files from a given location, then bake them into their final form.
	 * <p>
	 * Automatically handles sequentially managed file I/O and parallelized task deployment
	 */
	private static <BAKED> CompletableFuture<Map<ResourceLocation, BAKED>> bakeJsonResources(Executor backgroundExecutor, ResourceManager resourceManager, String assetPath,
																							 BiFunction<ResourceLocation, JsonObject, BAKED> elementFactory, Function<Throwable, BAKED> exceptionalFactory) {
		return loadResources(backgroundExecutor, resourceManager, assetPath, "json", GeckoLibResources::readJsonFile)
				.thenCompose(resources -> {
					List<CompletableFuture<Pair<ResourceLocation, BAKED>>> tasks = new ObjectArrayList<>(resources.size());

					resources.forEach(pair -> tasks.add(CompletableFuture.supplyAsync(() -> Pair.of(pair.left(), elementFactory.apply(pair.left(), pair.right())), backgroundExecutor)
																.exceptionally(ex -> Pair.of(pair.left(), exceptionalFactory.apply(ex)))));

					return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
							.thenApply(ignored -> tasks.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toMap(Pair::left, Pair::right)));
				});
	}

	/**
	 * Load a set of resources from their respective files for all available namespaces, into their raw/unbaked format ready for further processing.
	 * <p>
	 * This step is separated to prevent parallelized file I/O
	 */
	private static <UNBAKED> CompletableFuture<List<Pair<ResourceLocation, UNBAKED>>> loadResources(Executor executor, ResourceManager resourceManager, String assetPath, String fileType, BiFunction<ResourceLocation, Resource, UNBAKED> elementFactory) {
		final String fileTypeSuffix = "." + fileType;

		return CompletableFuture.supplyAsync(() -> resourceManager.listResources(assetPath, fileName -> fileName.getPath().endsWith(fileTypeSuffix)), executor)
				.thenCompose(resources -> {
					List<CompletableFuture<Pair<ResourceLocation, UNBAKED>>> tasks = new ObjectArrayList<>(resources.size());

					resources.forEach((path, resource) -> tasks.add(CompletableFuture.supplyAsync(() -> Pair.of(path, elementFactory.apply(path, resource)), executor)));

					return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).thenApply(ignored -> tasks.stream().map(CompletableFuture::join).filter(Objects::nonNull).toList());
				});
	}

	/**
	 * Bake a {@link BakedGeoModel} from its {@link JsonObject} serialized form
	 */
	@NotNull
	private static BakedGeoModel bakeModel(ResourceLocation path, JsonObject json) {
		if (path.getPath().endsWith(".animation.json"))
			throw new RuntimeException("Found animation file found in models folder! '" + path + "'");

		if (!path.getPath().endsWith(".geo.json"))
            GeckoLibConstants.LOGGER.warn("Found file in models folder with improper file name format; GeckoLib model files should end in .geo.json: '{}'", path);

		Model model = KeyFramesAdapter.GEO_GSON.fromJson(json, Model.class);
		ModelFormatVersion matchedVersion = ModelFormatVersion.match(model.formatVersion());

		if (matchedVersion == null) {
            GeckoLibConstants.LOGGER.warn("{}: Unknown geo model format version: '{}'. This may not work correctly", path, model.formatVersion());
		}
		else if (!matchedVersion.isSupported()) {
            GeckoLibConstants.LOGGER.error("{}: Unsupported geo model format version: '{}'. {}", path, model.formatVersion(), matchedVersion.getErrorMessage());
		}

		return BakedModelFactory.getForNamespace(path.getNamespace()).constructGeoModel(GeometryTree.fromModel(model));
	}

	/**
	 * Bake the {@link BakedAnimations} from a {@link JsonObject} serialized form
	 */
	@NotNull
	private static BakedAnimations bakeAnimations(ResourceLocation path, JsonObject json) {
		if (path.getPath().endsWith(".geo.json")) {
			throw new RuntimeException("Found model file in animations folder! '" + path + "'");
		}

		if (!path.getPath().endsWith(".animation.json"))
			GeckoLibConstants.LOGGER.warn("Found file in animations folder with improper file name format; animation files should end in .animation.json: '{}'", path);

		try {
			return KeyFramesAdapter.GEO_GSON.fromJson(GsonHelper.getAsJsonObject(json, "animations"), BakedAnimations.class);
		}
		catch (CompoundException ex) {
			throw ex.withMessage(path + ": Error building animations from JSON");
		}
		catch (Exception ex) {
			throw GeckoLibConstants.exception(path, "Error building animations from JSON", ex);
		}
	}

	/**
	 * Read a single resource into its {@link JsonObject} form
	 */
	private static JsonObject readJsonFile(ResourceLocation id, Resource resource) {
		try (Reader reader = resource.openAsReader()) {
			return GsonHelper.parse(reader);
		}
		catch (IOException ex) {
			throw GeckoLibConstants.exception(id, "Error reading JSON file", ex);
		}
	}
}
