package software.bernie.geckolib3.model;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.animation.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.model.GeoBone;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.MolangUtils;

import java.util.Collections;

public abstract class AnimatedGeoModel<T extends GeoAnimatable> extends GeoModelProvider<T>
		implements IAnimatableModel<T>, IAnimatableModelProvider<T> {
	private final AnimationProcessor animationProcessor;
	private GeoModel currentModel;

	protected AnimatedGeoModel() {
		this.animationProcessor = new AnimationProcessor(this);
	}

	public void registerBone(software.bernie.geckolib3.geo.render.built.GeoBone bone) {
		registerModelRenderer(bone);

		for (software.bernie.geckolib3.geo.render.built.GeoBone childBone : bone.childBones) {
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
		Minecraft mc = Minecraft.getInstance();
		AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
		AnimationEvent<T> predicate;
		double currentTick = animatable instanceof Entity livingEntity ? livingEntity.tickCount : getCurrentTick();

		if (manager.startTick == -1)
			manager.startTick = currentTick + mc.getFrameTime();

		if (!mc.isPaused() || manager.shouldPlayWhilePaused) {
			if (animatable instanceof LivingEntity) {
				manager.tick = currentTick + mc.getFrameTime();
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
			getAnimationProcessor().tickAnimation(animatable, instanceId, this.seekTime, predicate, GeckoLibCache.getInstance().parser, this.shouldCrashOnMissing);
	}

	public void codeAnimations(T entity, Integer uniqueID, AnimationEvent<?> customPredicate) {}

	@Override
	public AnimationProcessor getAnimationProcessor() {
		return this.animationProcessor;
	}

	public void registerModelRenderer(GeoBone modelRenderer) {
		this.animationProcessor.registerModelRenderer(modelRenderer);
	}

	@Override
	public Animation getAnimation(String name, GeoAnimatable animatable) {
		AnimationFile animation = GeckoLibCache.getInstance().getAnimations().get(this.getAnimationResource((T) animatable));

		if (animation == null) {
			throw new GeckoLibException(this.getAnimationResource((T) animatable),
					"Could not find animation file. Please double check name.");
		}

		return animation.getAnimation(name);
	}

	@Override
	public GeoModel getModel(ResourceLocation location) {
		GeoModel model = super.getModel(location);

		if (model == null) {
			throw new GeckoLibException(location,
					"Could not find model. If you are getting this with a built mod, please just restart your game.");
		}

		if (model != this.currentModel) {
			this.animationProcessor.clearModelRendererList();
			this.currentModel = model;

			for (software.bernie.geckolib3.geo.render.built.GeoBone bone : model.topLevelBones) {
				registerBone(bone);
			}
		}

		return model;
	}

	@Override
	public void setMolangQueries(GeoAnimatable animatable, double seekTime) {
		MolangParser parser = GeckoLibCache.getInstance().parser;
		Minecraft mc = Minecraft.getInstance();

		parser.setValue("query.actor_count", mc.level::getEntityCount);
		parser.setValue("query.time_of_day", () -> MolangUtils.normalizeTime(mc.level.getDayTime()));
		parser.setValue("query.moon_phase", mc.level::getMoonPhase);

		if (animatable instanceof Entity entity) {
			parser.setValue("query.distance_from_camera", () -> mc.gameRenderer.getMainCamera().getPosition().distanceTo(entity.position()));
			parser.setValue("query.is_on_ground", () -> MolangUtils.booleanToFloat(entity.isOnGround()));
			parser.setValue("query.is_in_water", () -> MolangUtils.booleanToFloat(entity.isInWater()));
			parser.setValue("query.is_in_water_or_rain", () -> MolangUtils.booleanToFloat(entity.isInWaterRainOrBubble()));

			if (entity instanceof LivingEntity livingEntity) {
				parser.setValue("query.health", livingEntity::getHealth);
				parser.setValue("query.max_health", livingEntity::getMaxHealth);
				parser.setValue("query.is_on_fire", () -> MolangUtils.booleanToFloat(livingEntity.isOnFire()));
				parser.setValue("query.ground_speed", () -> {
					Vec3 velocity = livingEntity.getDeltaMovement();

					return Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
				});
				parser.setValue("query.yaw_speed", () -> livingEntity.getViewYRot((float)seekTime - livingEntity.getViewYRot((float)seekTime - 0.1f)));
			}
		}
	}

	@Override
	public double getCurrentTick() {
		return Blaze3D.getTime() * 20;
	}
}
