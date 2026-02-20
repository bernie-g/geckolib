package software.bernie.geckolib.cache;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.cache.animation.Animation;
import software.bernie.geckolib.cache.animation.BakedAnimations;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.loading.loader.GeckoLibGsonLoader;
import software.bernie.geckolib.loading.loader.GeckoLibLoader;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/// Cache class for holding loaded [Animations][Animation] and [Models][GeoModel]
public final class GeckoLibResources implements PreparableReloadListener {
	public static final Identifier RELOAD_LISTENER_ID = GeckoLibConstants.id("geckolib_resources");
	public static final Identifier ANIMATIONS_PATH = GeckoLibConstants.id("geckolib/animations");
	public static final Identifier MODELS_PATH = GeckoLibConstants.id("geckolib/models");
	public static final Pattern SUFFIX_STRIPPER = Pattern.compile("((\\.geo)|((\\.animation)s?))?(\\.json)$");
	public static final Pattern PREFIX_STRIPPER = Pattern.compile("^(geckolib/)((animations/)|(models/))?");
	public static final PreparableReloadListener.StateKey<PendingResources> STATE_KEY = new PreparableReloadListener.StateKey<>();
	@SuppressWarnings("unchecked")
    private static Pair<GeckoLibLoader.Predicate, GeckoLibLoader<?>>[] LOADERS = new Pair[] {Pair.<GeckoLibLoader.Predicate, GeckoLibLoader<?>>of((_, _) -> true, new GeckoLibGsonLoader())};
	private static final Set<String> SUPPORTED_FILE_TYPES = new ObjectArraySet<>(1);

	private static BakedAnimationCache ANIMATIONS = new BakedAnimationCache(Collections.emptyMap());
	private static BakedModelCache MODELS = new BakedModelCache(Collections.emptyMap());

	/// Get GeckoLib's cache of all the loaded animations from the [#ANIMATIONS_PATH]
	public static BakedAnimationCache getBakedAnimations() {
		return ANIMATIONS;
	}

	/// Get GeckoLib's cache of all the loaded geo models from the [#MODELS_PATH]
	public static BakedModelCache getBakedModels() {
		return MODELS;
	}

	/// Add a `GeckoLibLoader` to the loaders array, for handling custom resource loading
	///
	/// This is an internal method, you should **<u>NOT</u>** be using this<br/>
	/// Use [GeckoLibUtil#addResourceLoader] instead.
	@SuppressWarnings("unchecked")
	@ApiStatus.Internal
	public static void addLoader(GeckoLibLoader.Predicate predicate, GeckoLibLoader<?> loader) {
		final Pair<GeckoLibLoader.Predicate, GeckoLibLoader<?>>[] copy = new Pair[LOADERS.length + 1];
		copy[LOADERS.length] = Pair.of(predicate, loader);

		System.arraycopy(LOADERS, 0, copy, 0, LOADERS.length);
		ArrayUtils.reverse(copy);

		LOADERS = copy;

        Collections.addAll(SUPPORTED_FILE_TYPES, loader.supportedExtensions());

		GeckoLibConstants.LOGGER.info("Added custom resource loader {}", loader.getClass().getSimpleName());
	}

	@Override
	public void prepareSharedState(SharedState sharedState) {
		sharedState.set(STATE_KEY, new PendingResources());
	}

	@ApiStatus.Internal
	@Override
	public CompletableFuture<Void> reload(PreparableReloadListener.SharedState sharedState, Executor prepExecutor, PreparationBarrier preparationBarrier, Executor applicationExecutor) {
		final ResourceManager resourceManager = sharedState.resourceManager();
		final PendingResources pending = sharedState.get(STATE_KEY);
		final MathParser mathParser = MathParser.createWithDeduplication();

		return CompletableFuture.runAsync(() -> CompletableFuture.allOf(loadModels(prepExecutor, resourceManager, pending),
																		loadAnimations(prepExecutor, resourceManager, pending, mathParser)), prepExecutor)
				.thenCompose(preparationBarrier::wait)
				.thenRunAsync(() -> applyResources(pending), applicationExecutor);
	}

	/// Provide a [Future] for retrieving and baking all animation JSONs from the [#ANIMATIONS_PATH]
	private static CompletableFuture<Map<Identifier, BakedAnimations>> loadAnimations(Executor executor, ResourceManager resourceManager, PendingResources pendingResources, MathParser mathParser) {
		return bakeAllResources(executor, resourceManager, ANIMATIONS_PATH.getPath(), GeckoLibLoader::deserializeGeckoLibAnimationFile,
								(loader, resourcePath, raw) -> loader.bakeGeckoLibAnimationsFile(resourcePath, raw, mathParser), () -> null)
				.whenComplete((map, err) -> pendingResources.complete(map, err, PendingResources::animations));
	}

	/// Provide a [Future] for retrieving and baking all geo model JSONs from the [#MODELS_PATH]
	private static CompletableFuture<Map<Identifier, BakedGeoModel>> loadModels(Executor executor, ResourceManager resourceManager, PendingResources pendingResources) {
		return bakeAllResources(executor, resourceManager, MODELS_PATH.getPath(), GeckoLibLoader::deserializeGeckoLibModelFile, GeckoLibLoader::bakeGeckoLibModelFile, () -> null)
				.whenComplete((map, err) -> pendingResources.complete(map, err, PendingResources::models));
	}

	/// Provide a [Future] for retrieving and baking all asset JSON files for a given type into a compiled map
	@SuppressWarnings("SameParameterValue")
    private static <UNBAKED, BAKED> CompletableFuture<Map<Identifier, BAKED>> bakeAllResources(Executor executor, ResourceManager resourceManager, String rootPath,
                                                                                               TriFunction<GeckoLibLoader<UNBAKED>, Identifier, Resource, UNBAKED> deserializer,
                                                                                               TriFunction<GeckoLibLoader<UNBAKED>, Identifier, UNBAKED, BAKED> bakery,
                                                                                               Supplier<@Nullable BAKED> onException) {
		return CompletableFuture.supplyAsync(() -> resourceManager.listResources(rootPath, GeckoLibResources::isFileTypeSupported), executor)
				.thenCompose(resources -> {
					final List<CompletableFuture<@Nullable Pair<Identifier, BAKED>>> tasks = new ObjectArrayList<>(resources.size());

					resources.forEach((path, resource) -> tasks.add(bakeResource(executor, path, resource, deserializer, bakery, onException)));

					return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
							.thenApply(_ -> tasks.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toMap(Pair::left, Pair::right)));
				});
	}

	/// Provide a [Future] to retrieve a single resource and fully bake it into a finalized object
	@SuppressWarnings({"unchecked"})
    private static <UNBAKED, BAKED> CompletableFuture<@Nullable Pair<Identifier, BAKED>> bakeResource(Executor executor, Identifier path, Resource resource,
																							TriFunction<GeckoLibLoader<UNBAKED>, Identifier, Resource, UNBAKED> deserializer,
																							TriFunction<GeckoLibLoader<UNBAKED>, Identifier, UNBAKED, BAKED> bakery,
																							Supplier<@Nullable BAKED> onException) {
		return CompletableFuture.supplyAsync(() -> (GeckoLibLoader<UNBAKED>)findLoaderForFile(path, resource), executor)
				.thenApply(loader -> Pair.of(stripPrefixAndSuffix(path), bakery.apply(loader, path, deserializer.apply(loader, path, resource))))
				.exceptionally(ex -> {
                    GeckoLibConstants.LOGGER.error("Error loading resource: {}", path, ex);

					BAKED replacement = onException.get();

					return replacement == null ? null : Pair.of(stripPrefixAndSuffix(path), replacement);
				});
	}

	/// Find the first applicable [GeckoLibLoader] for a given resource, based on its [Identifier] and [Resource]
	/// Allows for injected loaders to handle resource loading
	///
	/// @see GeckoLibUtil#addResourceLoader(GeckoLibLoader.Predicate, GeckoLibLoader)
	private static GeckoLibLoader<?> findLoaderForFile(Identifier id, Resource resource) {
		if (LOADERS.length > 1) {
			for (Pair<GeckoLibLoader.Predicate, GeckoLibLoader<?>> entry : LOADERS) {
				final GeckoLibLoader<?> loader = entry.second();

				if (anyExtensionMatches(id.getPath(), Arrays.asList(loader.supportedExtensions())) && entry.first().shouldHandle(id, resource))
					return entry.second();
			}
		}

		return LOADERS[0].second();
	}

	/// Strip the asset prefix and suffix from the given filepath, returning the stripped location
	///
	/// @return The stripped location, or the original path if no match is found
	public static Identifier stripPrefixAndSuffix(Identifier path) {
		String newPath = path.getPath();
		Matcher prefixMatcher = PREFIX_STRIPPER.matcher(newPath);
		newPath = prefixMatcher.find() ? newPath.substring(prefixMatcher.end()) : newPath;
		Matcher suffixMatcher = SUFFIX_STRIPPER.matcher(newPath);
		newPath = suffixMatcher.find() ? newPath.substring(0, suffixMatcher.start()) : newPath;

		return newPath.length() == path.getPath().length() ? path : path.withPath(newPath);
	}

	/// Apply the fully loaded resources to the publicly accessible maps for use
	private static void applyResources(PendingResources resources) {
		MODELS = new BakedModelCache(resources.models.join());
		ANIMATIONS = new BakedAnimationCache(resources.animations.join());

		GeckoLibConstants.LOGGER.info("Loaded {} models and {} animations from resources", MODELS.size(), ANIMATIONS.size());
	}

	/// @return Whether the given resource file is supported by the currently registered loaders
	private static boolean isFileTypeSupported(Identifier resourcePath) {
		final String path = resourcePath.getPath();

		if (SUPPORTED_FILE_TYPES.size() <= 3)
			return anyExtensionMatches(path, SUPPORTED_FILE_TYPES);

		return SUPPORTED_FILE_TYPES.contains(path.substring(path.lastIndexOf('.') + 1));
	}

	/// @return Whether the provided resource path matches any of the file type extensions in the provided [Iterable]
	private static boolean anyExtensionMatches(String path, Iterable<String> extensions) {
		for (String extension : extensions) {
			if (path.endsWith("." + extension))
				return true;
		}

		return false;
	}

	/// Resource-loading state holder object to pair with the associated [PreparableReloadListener.StateKey] for resource loading
	public record PendingResources(CompletableFuture<Map<Identifier, BakedGeoModel>> models, CompletableFuture<Map<Identifier, BakedAnimations>> animations) {
		public PendingResources() {
			this(new CompletableFuture<>(), new CompletableFuture<>());
		}

		/// Mark one of this instance's futures as complete, either with a result, or exceptionally
        public <T extends Map<Identifier, ? extends Object>> void complete(@Nullable T map, @Nullable Throwable error, Function<PendingResources, CompletableFuture<T>> target) {
			final CompletableFuture<T> future = target.apply(this);

			if (map != null) {
				future.complete(map);
			}
			else {
				future.completeExceptionally(error);
			}
		}
	}
}
