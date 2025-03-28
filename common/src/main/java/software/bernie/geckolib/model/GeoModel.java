package software.bernie.geckolib.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.GeckoLibResources;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;

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
	private double animTime;
	private double lastGameTickTime;
	private long lastRenderedInstance = -1;

	/**
	 * Returns the resource path for the {@link BakedGeoModel} (model json file) to render based on the provided animatable
	 */
	public abstract ResourceLocation getModelResource(T animatable, @Nullable GeoRenderer<T> renderer);

	/**
	 * Returns the resource path for the texture file to render based on the provided animatable
	 */
	public abstract ResourceLocation getTextureResource(T animatable, @Nullable GeoRenderer<T> renderer);

	/**
	 * Returns the resource path for the {@link BakedAnimations} (animation json file) to use for animations based on the provided animatable
	 */
	public abstract ResourceLocation getAnimationResource(T animatable);

	/**
	 * Returns the resource path for the {@link BakedAnimations} (animation json file) fallback locations in the event
	 * your animation isn't present in the {@link #getAnimationResource(GeoAnimatable) primary resource}.
	 * <p>
	 * Should <b><u>NOT</u></b> be used as the primary animation resource path, and in general shouldn't be used
	 * at all unless you know what you are doing
	 */
	public ResourceLocation[] getAnimationResourceFallbacks(T animatable, GeoRenderer<T> renderer) {
		return new ResourceLocation[0];
	}

	/**
	 * Override this and return true if Geckolib should crash when attempting to animate the model, but fails to find a bone
	 * <p>
	 * By default, GeckoLib will just gracefully ignore a missing bone, which might cause oddities with incorrect models or mismatching variables
	 */
	public boolean crashIfBoneMissing() {
		return false;
	}

	/**
	 * Gets the default render type for this animatable, to be selected by default by the renderer using it
	 *
	 * @return Return the RenderType to use, or null to prevent the model rendering. Returning null will not prevent animation functions taking place
	 */
	@Nullable
	public RenderType getRenderType(T animatable, ResourceLocation texture) {
		return RenderType.entityCutoutNoCull(texture);
	}

	/**
	 * Get the baked geo model object used for rendering from the given resource path
	 */
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		BakedGeoModel model = GeckoLibResources.getBakedModels().get(location);

		if (model == null) {
			if (!location.getPath().contains("geo/"))
				throw GeckoLibConstants.exception(location, "Invalid model resource path provided - GeckoLib models must be placed in assets/<modid>/geo/");

			throw GeckoLibConstants.exception(location, "Unable to find model");
		}

		if (model != this.currentModel) {
			this.processor.setActiveModel(model);
			this.currentModel = model;
		}

		return this.currentModel;
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
	 * Gets the loaded {@link Animation} for the given animation {@code name}, if it exists
	 *
	 * @param animatable The {@code GeoAnimatable} instance being referred to
	 * @param name The name of the animation to retrieve
	 * @return The {@code Animation} instance for the provided {@code name}, or null if none match
	 */
	@Nullable
	public Animation getAnimation(T animatable, String name) {
		ResourceLocation location = getAnimationResource(animatable);
		BakedAnimations bakedAnimations = GeckoLibResources.getBakedAnimations().get(location);
		Animation animation = bakedAnimations != null ? bakedAnimations.getAnimation(name) : null;

		if (animation != null)
			return animation;

		for (ResourceLocation fallbackLocation : getAnimationResourceFallbacks(animatable, null)) {
			bakedAnimations = GeckoLibResources.getBakedAnimations().get(location = fallbackLocation);
			animation = bakedAnimations != null ? bakedAnimations.getAnimation(name) : null;

			if (animation != null)
				return animation;
		}

		if (bakedAnimations == null) {
			if (!location.getPath().contains("animations/"))
				throw GeckoLibConstants.exception(location, "Invalid animation resource path provided - GeckoLib animations must be placed in assets/<modid>/animations/");

			throw GeckoLibConstants.exception(location, "Unable to find animation file.");
		}

		return null;
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
	 * @param instanceId The unique instance id of the animatable being animated
	 * @param dataConsumer The DataTicket + data consumer to be added to the AnimationEvent
	 */
	public void addAdditionalStateData(T animatable, long instanceId, BiConsumer<DataTicket<T>, T> dataConsumer) {}

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered
	 * <p>
	 * It is an internal method for automated animation parsing. Use {@link GeoModel#setCustomAnimations(GeoAnimatable, long, AnimationState)} for custom animation work
	 */
	@ApiStatus.Internal
	public void handleAnimations(T animatable, long instanceId, AnimationState<T> animationState, float partialTick) {
		Minecraft mc = Minecraft.getInstance();
		AnimatableManager<T> animatableManager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);
		Double currentTick = animationState.getData(DataTickets.TICK);

		if (currentTick == null)
			currentTick = animatable instanceof Entity entity ? (double)entity.tickCount : RenderUtil.getCurrentTick();

		if (animatableManager.getFirstTickTime() == -1)
			animatableManager.startedAt(currentTick + partialTick);

		double currentFrameTime = animatable instanceof Entity || animatable instanceof GeoReplacedEntity ? currentTick + partialTick : currentTick - animatableManager.getFirstTickTime();
		boolean isReRender = !animatableManager.isFirstTick() && currentFrameTime == animatableManager.getLastUpdateTime();

		if (isReRender && instanceId == this.lastRenderedInstance)
			return;

		if (!mc.isPaused() || animatable.shouldPlayAnimsWhileGamePaused()) {
			animatableManager.updatedAt(currentFrameTime);

			double lastUpdateTime = animatableManager.getLastUpdateTime();
			this.animTime += lastUpdateTime - this.lastGameTickTime;
			this.lastGameTickTime = lastUpdateTime;
		}

		animationState.animationTick = this.animTime;
		this.lastRenderedInstance = instanceId;
		AnimationProcessor<T> processor = getAnimationProcessor();

		processor.preAnimationSetup(animationState, this.animTime);

		if (!processor.getRegisteredBones().isEmpty())
			processor.tickAnimation(animatable, this, animatableManager, this.animTime, animationState, crashIfBoneMissing());

		setCustomAnimations(animatable, instanceId, animationState);
	}

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered
	 * <p>
	 * Override to set custom animations (such as head rotation, etc)
	 *
	 * @param animatable The {@code GeoAnimatable} instance currently being rendered
	 * @param instanceId The instance id of the {@code GeoAnimatable}
	 * @param animationState An {@link AnimationState} instance created to hold animation data for the {@code animatable} for this method call
	 */
	public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {}

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered
	 * <p>
	 * Use this method to set custom {@link software.bernie.geckolib.loading.math.value.Variable Variable} values via
	 * {@link software.bernie.geckolib.loading.math.MathParser#setVariable(String, DoubleSupplier) MathParser.setVariable}
	 *
	 * @param animationState The AnimationState data for the current render frame
	 * @param animTime The internal tick counter kept by the {@link AnimatableManager manager} for this animatable
	 */
	public void applyMolangQueries(AnimationState<T> animationState, double animTime) {}
}
