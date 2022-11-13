package software.bernie.geckolib3.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.GeckoLibException;
import software.bernie.geckolib3.cache.GeckoLibCache;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.cache.object.GeoBone;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animatable.model.CoreGeoModel;
import software.bernie.geckolib3.core.animation.Animation;
import software.bernie.geckolib3.core.animation.AnimatableManager;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.core.animation.AnimationProcessor;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.molang.MolangQueries;
import software.bernie.geckolib3.core.object.DataTicket;
import software.bernie.geckolib3.loading.object.BakedAnimations;
import software.bernie.geckolib3.renderer.GeoRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Base class for all code-based model objects.<br>
 * All models to registered to a {@link GeoRenderer} should be an instance of this or one of its subclasses.
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Models">GeckoLib Wiki - Models</a>
 */
public abstract class GeoModel<T extends GeoAnimatable> implements CoreGeoModel<T> {
	private final AnimationProcessor<T> processor = new AnimationProcessor<>(this);

	private BakedGeoModel currentModel = null;
	private double seekTime;
	private double lastGameTickTime;

	/**
	 * Returns the resource path for the {@link BakedGeoModel} (model json file) to render based on the provided animatable
	 */
	public abstract ResourceLocation getModelResource(T animatable);

	/**
	 * Returns the resource path for the texture file to render based on the provided animatable
	 */
	public abstract ResourceLocation getTextureResource(T animatable);

	/**
	 * Returns the resourcepath for the {@link BakedAnimations} (animation json file) to use for animations based on the provided animatable
	 */
	public abstract ResourceLocation getAnimationResource(T animatable);

	/**
	 * Override this and return true if Geckolib should crash when attempting to animate the model, but fails to find a bone.<br>
	 * By default, GeckoLib will just gracefully ignore a missing bone, which might cause oddities with incorrect models or mismatching variables.<br>
	 */
	public boolean crashIfBoneMissing() {
		return false;
	}

	/**
	 * Gets the default render type for this animatable, to be selected by default by the renderer using it
	 */
	public RenderType getRenderType(T animatable, ResourceLocation texture) {
		return RenderType.entityCutoutNoCull(texture);
	}

	@Override
	public final BakedGeoModel getBakedGeoModel(String location) {
		return getBakedModel(new ResourceLocation(location));
	}

	/**
	 * Get the baked geo model object used for rendering from the given resource path
	 */
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		BakedGeoModel model = GeckoLibCache.getBakedModels().get(location);

		if (model == null)
			throw new GeckoLibException(location, "Unable to find model");

		if (model != this.currentModel) {
			this.processor.setActiveModel(model);
			this.currentModel = model;
		}

		return this.currentModel;
	}

	/**
	 * Gets a bone from this model by name.<br>
	 * Generally not a very efficient method, should be avoided where possible.
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link software.bernie.geckolib3.cache.object.GeoBone} if one matches, otherwise an empty Optional
	 */
	public Optional<software.bernie.geckolib3.cache.object.GeoBone> getBone(String name) {
		return Optional.ofNullable((GeoBone)getAnimationProcessor().getBone(name));
	}

	/**
	 * Get the baked animation object used for rendering from the given resource path
	 */
	@Override
	public Animation getAnimation(T animatable, String name) {
		ResourceLocation location = getAnimationResource(animatable);
		BakedAnimations bakedAnimations = GeckoLibCache.getBakedAnimations().get(location);

		if (bakedAnimations == null)
			throw new GeckoLibException(location, "Unable to find animation.");

		return bakedAnimations.getAnimation(name);
	}

	@Override
	public AnimationProcessor<T> getAnimationProcessor() {
		return this.processor;
	}

	/**
	 * Add additional {@link DataTicket DataTickets} to the {@link AnimationEvent} to be handled by your animation handler at render time
	 * @param animatable The animatable instance currently being animated
	 * @param instanceId The unique instance id of the animatable being animated
	 * @param dataConsumer The DataTicket + data consumer to be added to the AnimationEvent
	 */
	public void addAdditionalEventData(T animatable, long instanceId, BiConsumer<DataTicket<T>, T> dataConsumer) {}

	@Override
	public final void handleAnimations(T animatable, long instanceId, AnimationEvent<T> animationEvent) {
		Minecraft mc = Minecraft.getInstance();
		AnimatableManager<T> animatableManager = animatable.getFactory().getManagerForId(instanceId);
		double currentTick = animatable instanceof Entity livingEntity ? livingEntity.tickCount : RenderUtils.getCurrentTick();

		if (animatableManager.getFirstTickTime() == -1)
			animatableManager.startedAt(currentTick + mc.getFrameTime());

		if (!mc.isPaused() || animatable.shouldPlayAnimsWhileGamePaused()) {
			if (animatable instanceof LivingEntity) {
				animatableManager.updatedAt(currentTick + mc.getFrameTime());
			}
			else {
				animatableManager.updatedAt(currentTick - animatableManager.getFirstTickTime());
			}

			double gameTick = animatableManager.getLastUpdateTime();
			this.seekTime += gameTick - this.lastGameTickTime;
			this.lastGameTickTime = gameTick;
		}

		animationEvent.animationTick = this.seekTime;
		AnimationProcessor<T> processor = getAnimationProcessor();

		processor.preAnimationSetup(animationEvent.getAnimatable(), this.seekTime);

		if (!processor.getRegisteredBones().isEmpty())
			processor.tickAnimation(animatable, this, animatableManager, this.seekTime, animationEvent, crashIfBoneMissing());

		setCustomAnimations(animatable, instanceId, animationEvent);
	}

	@Override
	public void applyMolangQueries(T animatable, double seekTime) {
		MolangParser parser = MolangParser.INSTANCE;
		Minecraft mc = Minecraft.getInstance();

		parser.setValue(MolangQueries.ANIM_TIME, () -> seekTime / 20d);
		parser.setValue(MolangQueries.LIFE_TIME, () -> seekTime / 20d);
		parser.setValue(MolangQueries.ACTOR_COUNT, mc.level::getEntityCount);
		parser.setValue(MolangQueries.TIME_OF_DAY, () -> mc.level.getDayTime() / 24000f);
		parser.setValue(MolangQueries.MOON_PHASE, mc.level::getMoonPhase);

		if (animatable instanceof Entity entity) {
			parser.setValue(MolangQueries.DISTANCE_FROM_CAMERA, () -> mc.gameRenderer.getMainCamera().getPosition().distanceTo(entity.position()));
			parser.setValue(MolangQueries.IS_ON_GROUND, () -> RenderUtils.booleanToFloat(entity.isOnGround()));
			parser.setValue(MolangQueries.IS_IN_WATER, () -> RenderUtils.booleanToFloat(entity.isInWater()));
			parser.setValue(MolangQueries.IS_IN_WATER_OR_RAIN, () -> RenderUtils.booleanToFloat(entity.isInWaterRainOrBubble()));

			if (entity instanceof LivingEntity livingEntity) {
				parser.setValue(MolangQueries.HEALTH, livingEntity::getHealth);
				parser.setValue(MolangQueries.MAX_HEALTH, livingEntity::getMaxHealth);
				parser.setValue(MolangQueries.IS_ON_FIRE, () -> RenderUtils.booleanToFloat(livingEntity.isOnFire()));
				parser.setValue(MolangQueries.GROUND_SPEED, () -> {
					Vec3 velocity = livingEntity.getDeltaMovement();

					return Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
				});
				parser.setValue(MolangQueries.YAW_SPEED, () -> livingEntity.getViewYRot((float)seekTime - livingEntity.getViewYRot((float)seekTime - 0.1f)));
			}
		}
	}
}
