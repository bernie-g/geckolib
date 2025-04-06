package software.bernie.geckolib.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationProcessor;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;

import java.util.Map;
import java.util.Optional;
import java.util.function.ToDoubleFunction;

/**
 * Base class for all code-based model objects
 * <p>
 * All models to registered to a {@link GeoRenderer} should be an instance of this or one of its subclasses
 *
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Models">GeckoLib Wiki - Models</a>
 */
public abstract class GeoModel<T extends GeoAnimatable> {
	private final AnimationProcessor<T> processor = new AnimationProcessor<>(this);

	private BakedGeoModel currentModel = null;
	private double animationTicks;
	private double lastGameTickTime;
	private long lastRenderedInstance = -1;

	/**
	 * Returns the resource ID for the {@link BakedGeoModel} (model JSON file) to render based on the provided {@link GeoRenderState}
	 */
	public abstract ResourceLocation getModelResource(GeoRenderState renderState);

	/**
	 * Returns the resource path for the texture file to render based on the provided {@link GeoRenderState}
	 */
	public abstract ResourceLocation getTextureResource(GeoRenderState renderState);

	/**
	 * Returns the resource ID for the {@link BakedAnimations} (animation JSON file) to use for animations based on the provided animatable
	 */
	public abstract ResourceLocation getAnimationResource(T animatable);

	/**
	 * Returns the resource path for the {@link BakedAnimations} (animation JSON file) fallback locations in the event
	 * your animation isn't present in the {@link #getAnimationResource(GeoAnimatable) primary resource}.
	 * <p>
	 * Should <b><u>NOT</u></b> be used as the primary animation resource path, and in general shouldn't be used
	 * at all unless you know what you are doing
	 */
	public ResourceLocation[] getAnimationResourceFallbacks(T animatable) {
		return new ResourceLocation[0];
	}

	/**
	 * Gets the default render type for this animatable, to be selected by default by the renderer using it
	 *
	 * @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions taking place
	 */
	@Nullable
	public RenderType getRenderType(GeoRenderState renderState, ResourceLocation texture) {
		return RenderType.entityCutoutNoCull(texture);
	}

	/**
	 * Gets a bone from this model by name
	 *
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link software.bernie.geckolib.cache.object.GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<GeoBone> getBone(String name) {
		return Optional.ofNullable(getAnimationProcessor().getBone(name));
	}

	/**
	 * Gets the {@link AnimationProcessor} for this model.
	 */
	public AnimationProcessor<T> getAnimationProcessor() {
		return this.processor;
	}

	/**
	 * Add additional {@link DataTicket DataTickets} to the {@link AnimationState} to be handled by your animation handler at render time
	 *
	 * @param animatable The animatable instance currently being animated
	 * @param renderState The GeoRenderState to capture data in
	 */
	public void addAdditionalStateData(T animatable, GeoRenderState renderState) {}

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered
	 * <p>
	 * Override to set custom animations (such as head rotation, etc)
	 *
	 * @param animationState An {@link AnimationState} instance created to hold animation data for the animatable for this render pass
	 */
	public void setCustomAnimations(AnimationState<T> animationState) {}

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered
	 * <p>
	 * Use this method to set custom {@link software.bernie.geckolib.loading.math.value.Variable Variable} values via
	 * {@link software.bernie.geckolib.loading.math.MathParser#setVariable(String, ToDoubleFunction) MathParser.setVariable}
	 *
	 * @param animatable The {@link GeoAnimatable} instance about to be rendered
	 */
	public void applyMolangQueries(T animatable) {}

	/**
	 * Get the baked geo model object used for rendering from the given resource path
	 */
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		BakedGeoModel model = GeckoLibResources.getBakedModels().get(location);

		if (model == null) {
			ResourceLocation strippedPath = GeckoLibResources.stripPrefixAndSuffix(location);

			if (!location.equals(strippedPath)) {
				GeckoLibConstants.LOGGER.debug("Unnecessary prefix or suffix found in model resource path: {} ({}). Remove this from your getModelResource",
											   location.getPath(), location.getPath().replace(strippedPath.getPath(), ""));

				model = GeckoLibResources.getBakedModels().get(location = strippedPath);
			}

			if (model == null)
				throw new IllegalArgumentException("Unable to find model file: " + location);
		}

		if (model != this.currentModel) {
			this.processor.setActiveModel(model);
			this.currentModel = model;
		}

		return this.currentModel;
	}

	/**
	 * Gets the loaded {@link Animation} for the given animation {@code name}, if it exists
	 *
	 * @param animatable The {@link GeoAnimatable} for the upcoming render pass
	 * @param name The name of the animation to retrieve
	 * @return The Animation instance for the provided {@code name}, or null if none match
	 */
	@Nullable
	public Animation getAnimation(T animatable, String name) throws RuntimeException {
		ResourceLocation location = getAnimationResource(animatable);
		ResourceLocation[] fallbackLocations = getAnimationResourceFallbacks(animatable);
		Map<ResourceLocation, BakedAnimations> animations = GeckoLibResources.getBakedAnimations();
		int fallbackIndex = -1;
		BakedAnimations bakedAnimations;

		do {
			bakedAnimations = animations.get(location);

			if (bakedAnimations == null) {
				ResourceLocation strippedPath = GeckoLibResources.stripPrefixAndSuffix(location);

				if (!strippedPath.equals(location)) {
					GeckoLibConstants.LOGGER.debug("Unnecessary prefix or suffix found in animations resource path: {} ({}). Remove this from your getAnimationResource",
												   location.getPath(), location.getPath().replace(strippedPath.getPath(), ""));

					bakedAnimations = animations.get(strippedPath);
				}
			}

			if (bakedAnimations != null) {
				Animation animation = bakedAnimations.getAnimation(name);

				if (animation != null)
					return animation;
			}

			location = fallbackLocations[++fallbackIndex];
		}
		while (fallbackIndex < fallbackLocations.length - 2);

		if (bakedAnimations == null)
			throw new IllegalArgumentException("Unable to find animation file '" + location + "' for animatable '" + animatable.getClass().getName() + "'");

		GeckoLibConstants.LOGGER.error("Unable to find animation: '{}' in animation file '{}' for animatable '{}'", name, location, animatable.getClass().getName());

		return null;
	}

	/**
	 * Perform the necessary preparations for the upcoming render pass
	 */
	@ApiStatus.Internal
	public void prepareForRenderPass(T animatable, GeoRenderState renderState) {
		Minecraft mc = Minecraft.getInstance();
		long instanceId = renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID);
		AnimatableManager<T> animatableManager = renderState.getGeckolibData(DataTickets.ANIMATABLE_MANAGER);
		EntityRenderState entityRenderState = renderState instanceof EntityRenderState state ? state : null;
		double animatableRenderTime = entityRenderState != null ? entityRenderState.ageInTicks : renderState.getGeckolibData(DataTickets.TICK);

		if (animatableManager.getFirstTickTime() == -1)
			animatableManager.startedAt(animatableRenderTime);

		double currentFrameTime = entityRenderState != null ? animatableRenderTime : animatableRenderTime - animatableManager.getFirstTickTime();
		boolean isReRender = !animatableManager.isFirstTick() && currentFrameTime == animatableManager.getLastUpdateTime();

		if (isReRender && instanceId == this.lastRenderedInstance)
			return;

		if (!mc.isPaused() || animatable.shouldPlayAnimsWhileGamePaused()) {
			animatableManager.updatedAt(currentFrameTime);

			double lastUpdateTime = animatableManager.getLastUpdateTime();
			this.animationTicks += lastUpdateTime - this.lastGameTickTime;
			this.lastGameTickTime = lastUpdateTime;
		}

		this.lastRenderedInstance = instanceId;

		addAdditionalStateData(animatable, renderState);
		renderState.addGeckolibData(DataTickets.ANIMATION_TICKS, this.animationTicks);
		applyMolangQueries(animatable);
		getAnimationProcessor().prepareForRenderPass(animatable, animatableManager, renderState, this.animationTicks, this);
	}

	/**
	 * This method is called once per-render frame for each {@link GeoAnimatable} being rendered
	 * <p>
	 * It is an internal method for automated animation parsing. Use {@link GeoModel#setCustomAnimations(AnimationState)} for custom animation work
	 */
	@ApiStatus.Internal
	public void handleAnimations(AnimationState<T> animationState) {
		AnimationProcessor<T> processor = getAnimationProcessor();

		if (!processor.getRegisteredBones().isEmpty())
			processor.tickAnimation(animationState);

		setCustomAnimations(animationState);
	}
}
