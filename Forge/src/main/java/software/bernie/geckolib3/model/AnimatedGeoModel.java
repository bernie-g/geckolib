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
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.animation.AnimationProcessor;
import software.bernie.geckolib3.core.animatable.model.GeoBone;
import software.bernie.geckolib3.core.molang.MolangQueries;
import software.bernie.geckolib3.loading.object.BakedAnimations;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.BakedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModel;
import software.bernie.geckolib3.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.MolangUtils;

import java.util.Collections;

public abstract class AnimatedGeoModel<T extends GeoAnimatable> extends GeoModel<T>
		implements IAnimatableModel<T>, IAnimatableModelProvider<T> {
	private final AnimationProcessor animationProcessor;
	private BakedGeoModel currentModel;

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

		if (!getAnimationProcessor().getRegisteredBones().isEmpty())
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
		BakedAnimations animation = GeckoLibCache.getBakedAnimations().get(this.getAnimationResource((T) animatable));

		if (animation == null) {
			throw new GeckoLibException(this.getAnimationResource((T) animatable),
					"Could not find animation file. Please double check name.");
		}

		return animation.getAnimation(name);
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		BakedGeoModel model = super.getBakedModel(location);

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
		MolangParser parser = MolangParser.INSTANCE;
		Minecraft mc = Minecraft.getInstance();

		parser.setValue(MolangQueries.ANIM_TIME, () -> seekTime / 20d);
		parser.setValue(MolangQueries.LIFE_TIME, () -> seekTime / 20d);
		parser.setValue(MolangQueries.ACTOR_COUNT, mc.level::getEntityCount);
		parser.setValue(MolangQueries.TIME_OF_DAY, () -> MolangUtils.normalizeTime(mc.level.getDayTime()));
		parser.setValue(MolangQueries.MOON_PHASE, mc.level::getMoonPhase);

		if (animatable instanceof Entity entity) {
			parser.setValue(MolangQueries.DISTANCE_FROM_CAMERA, () -> mc.gameRenderer.getMainCamera().getPosition().distanceTo(entity.position()));
			parser.setValue(MolangQueries.IS_ON_GROUND, () -> MolangUtils.booleanToFloat(entity.isOnGround()));
			parser.setValue(MolangQueries.IS_IN_WATER, () -> MolangUtils.booleanToFloat(entity.isInWater()));
			parser.setValue(MolangQueries.IS_IN_WATER_OR_RAIN, () -> MolangUtils.booleanToFloat(entity.isInWaterRainOrBubble()));

			if (entity instanceof LivingEntity livingEntity) {
				parser.setValue(MolangQueries.HEALTH, livingEntity::getHealth);
				parser.setValue(MolangQueries.MAX_HEALTH, livingEntity::getMaxHealth);
				parser.setValue(MolangQueries.IS_ON_FIRE, () -> MolangUtils.booleanToFloat(livingEntity.isOnFire()));
				parser.setValue(MolangQueries.GROUND_SPEED, () -> {
					Vec3 velocity = livingEntity.getDeltaMovement();

					return Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
				});
				parser.setValue(MolangQueries.YAW_SPEED, () -> livingEntity.getViewYRot((float)seekTime - livingEntity.getViewYRot((float)seekTime - 0.1f)));
			}
		}
	}

	@Override
	public double getCurrentTick() {
		return Blaze3D.getTime() * 20;
	}
}
