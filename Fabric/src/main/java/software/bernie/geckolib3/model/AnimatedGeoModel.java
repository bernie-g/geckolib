package software.bernie.geckolib3.model;

import java.util.Collections;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.AnimationTicker;
import software.bernie.geckolib3.util.MolangUtils;

public abstract class AnimatedGeoModel<T extends IAnimatable> extends GeoModelProvider<T>
		implements IAnimatableModel<T>, IAnimatableModelProvider<T> {
	private final AnimationProcessor animationProcessor;
	private GeoModel currentModel;

	protected AnimatedGeoModel() {
		this.animationProcessor = new AnimationProcessor(this);
	}

	public void registerBone(GeoBone bone) {
		registerModelRenderer(bone);

		for (GeoBone childBone : bone.childBones) {
			registerBone(childBone);
		}
	}

	/**
	 * Use {@link IAnimatableModel#setCustomAnimations(Object, int, AnimationEvent)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	@Override
	public void setLivingAnimations(T animatable, Integer instanceId, AnimationEvent animationEvent) {
		this.setCustomAnimations(animatable, instanceId.intValue(), animationEvent);
	}

	@Override
	public void setCustomAnimations(T animatable, int instanceId, AnimationEvent animationEvent) {
		MinecraftClient mc = MinecraftClient.getInstance();
		AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
		AnimationEvent<T> predicate;
		double currentTick = animatable instanceof Entity livingEntity ? livingEntity.age : getCurrentTick();

		if (manager.ticker == null && !(animatable instanceof LivingEntity)) 
			manager.ticker = new AnimationTicker(manager);
		
		if (manager.startTick == -1)
			manager.startTick = currentTick + mc.getTickDelta();

		if (!mc.isPaused() || manager.shouldPlayWhilePaused) {
			if (animatable instanceof LivingEntity) {
				manager.tick = currentTick + mc.getTickDelta();
				double gameTick = manager.tick;
				double deltaTicks = gameTick - this.lastGameTickTime;
				this.seekTime += deltaTicks;
				this.lastGameTickTime = gameTick;

				codeAnimations(animatable, instanceId, animationEvent);
			} else {
				manager.tick = currentTick - manager.startTick;
				double gameTick = manager.tick;
				double deltaTicks = gameTick - this.lastGameTickTime;
				this.seekTime += deltaTicks;
				this.lastGameTickTime = gameTick;
			}
		}

		predicate = animationEvent == null ? new AnimationEvent<T>(animatable, 0, 0, (float)(manager.tick - this.lastGameTickTime), false, Collections.emptyList()) : animationEvent;
		predicate.animationTick = this.seekTime;

		getAnimationProcessor().preAnimationSetup(predicate.getAnimatable(), this.seekTime);

		if (!getAnimationProcessor().getModelRendererList().isEmpty())
			getAnimationProcessor().tickAnimation(animatable, instanceId, this.seekTime, predicate,
					GeckoLibCache.getInstance().parser, this.shouldCrashOnMissing);
	}

	public void codeAnimations(T entity, Integer uniqueID, AnimationEvent<?> customPredicate) {}

	@Override
	public AnimationProcessor getAnimationProcessor() {
		return this.animationProcessor;
	}

	public void registerModelRenderer(IBone modelRenderer) {
		this.animationProcessor.registerModelRenderer(modelRenderer);
	}

	@Override
	public Animation getAnimation(String name, IAnimatable animatable) {
		AnimationFile animation = GeckoLibCache.getInstance().getAnimations().get(this.getAnimationFileLocation((T) animatable));

		if (animation == null) {
			throw new GeckoLibException(this.getAnimationFileLocation((T) animatable),
					"Could not find animation file. Please double check name.");
		}

		return animation.getAnimation(name);
	}

	@Override
	public GeoModel getModel(Identifier location) {
		GeoModel model = super.getModel(location);

		if (model == null) {
			throw new GeckoLibException(location,
					"Could not find model. If you are getting this with a built mod, please just restart your game.");
		}

		if (model != this.currentModel) {
			this.animationProcessor.clearModelRendererList();
			this.currentModel = model;

			for (GeoBone bone : model.topLevelBones) {
				registerBone(bone);
			}
		}

		return model;
	}

	@Override
	public void setMolangQueries(IAnimatable animatable, double seekTime) {
		MolangParser parser = GeckoLibCache.getInstance().parser;
		MinecraftClient mc = MinecraftClient.getInstance();

		parser.setValue("query.actor_count", mc.world::getRegularEntityCount);
		parser.setValue("query.time_of_day", () -> MolangUtils.normalizeTime(mc.world.getTimeOfDay()));
		parser.setValue("query.moon_phase", mc.world::getMoonPhase);

		if (animatable instanceof Entity entity) {
			parser.setValue("query.distance_from_camera",
					() -> mc.gameRenderer.getCamera().getPos().distanceTo(entity.getPos()));
			parser.setValue("query.is_on_ground", () -> MolangUtils.booleanToFloat(entity.isOnGround()));
			parser.setValue("query.is_in_water", () -> MolangUtils.booleanToFloat(entity.isTouchingWater()));
			parser.setValue("query.is_in_water_or_rain",
					() -> MolangUtils.booleanToFloat(entity.isWet()));

			if (entity instanceof LivingEntity livingEntity) {
				parser.setValue("query.health", livingEntity::getHealth);
				parser.setValue("query.max_health", livingEntity::getMaxHealth);
				parser.setValue("query.is_on_fire", () -> MolangUtils.booleanToFloat(livingEntity.isOnFire()));
				parser.setValue("query.ground_speed", () -> {
					Vec3d velocity = livingEntity.getVelocity();

					return MathHelper.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
				});
				parser.setValue("query.yaw_speed", () -> livingEntity
						.getYaw((float) seekTime - livingEntity.getYaw((float) seekTime - 0.1f)));
			}
		}
	}
}