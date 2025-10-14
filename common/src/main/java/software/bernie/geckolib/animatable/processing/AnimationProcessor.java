package software.bernie.geckolib.animatable.processing;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableObject;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.loading.math.value.Variable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.ClientUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class AnimationProcessor<T extends GeoAnimatable> {
	private final Map<String, GeoBone> bones = new Object2ObjectOpenHashMap<>();
	private final GeoModel<T> model;

	public boolean reloadAnimations = false;

	public AnimationProcessor(GeoModel<T> model) {
		this.model = model;
	}

	/**
	 * Perform the necessary prepations for the upcoming render pass
	 *
	 * @param animatable The animatable relevant to the upcoming render pass
	 * @param animatableManager The manager instance for the animatable for the upcoming render pass
	 * @param renderState The {@link GeoRenderState} being built for the upcoming render pass
	 * @param lerpedAnimationTick The current tick + partial tick for the animatable
	 * @param model The GeoModel tasked for the upcoming render pass
	 */
	public void prepareForRenderPass(T animatable, AnimatableManager<T> animatableManager, GeoRenderState renderState, double lerpedAnimationTick, GeoModel<T> model) {
		MolangQueries.Actor<T> actor = new MolangQueries.Actor<>(animatable, renderState, new MutableObject<>(), lerpedAnimationTick, renderState.getGeckolibData(DataTickets.PARTIAL_TICK), ClientUtil.getLevel(), ClientUtil.getClientPlayer(), ClientUtil.getCameraPos());
		Reference2DoubleMap<Variable> variables = new Reference2DoubleOpenHashMap<>();

		renderState.addGeckolibData(DataTickets.QUERY_VALUES, variables);

		for (AnimationController<T> controller : animatableManager.getAnimationControllers().values()) {
			actor.controller().setValue(controller);
			controller.prepareForRenderPass(animatable, animatableManager, actor, variables, lerpedAnimationTick, model);
		}
	}

	/**
	 * Build an animation queue for the given {@link RawAnimation}
	 *
	 * @param animatable The {@link GeoAnimatable} for the upcoming render pass
	 * @param rawAnimation The raw animation to be compiled
	 * @return A queue of animations and loop types to play
	 */
	public Queue<QueuedAnimation> buildAnimationQueue(T animatable, RawAnimation rawAnimation) {
		LinkedList<QueuedAnimation> animations = new LinkedList<>();
		boolean error = false;

		for (RawAnimation.Stage stage : rawAnimation.getAnimationStages()) {
			Animation animation = null;

			// This is intentional. DO NOT CHANGE THIS or Tslat will be unhappy
            //noinspection StringEquality
            if (stage.animationName() == RawAnimation.Stage.WAIT) {
				animation = Animation.generateWaitAnimation(stage.additionalTicks());
			}
			else {
				try {
					animation = this.model.getAnimation(animatable, stage.animationName());
				}
				catch (RuntimeException ex) {
					GeckoLibConstants.LOGGER.error("Error while retrieving animation for animatable '{}'", animatable.getClass().getName(), ex);

					error = true;
				}
			}

			if (animation != null)
				animations.add(new QueuedAnimation(animation, stage.loopType()));
		}

		return error ? null : animations;
	}

	/**
	 * Tick and apply transformations to the model based on the current state of the {@link AnimationController}
	 *
	 * @param animationState The AnimationState for the current render pass
	 */
	public void tickAnimation(AnimationState<T> animationState) {
		AnimatableManager<T> animatableManager = animationState.manager();
		Map<String, BoneSnapshot> boneSnapshots = updateBoneSnapshots(animatableManager.getBoneSnapshotCollection());
		double lerpedAnimationTick = animationState.getData(DataTickets.ANIMATION_TICKS);

		for (AnimationController<T> controller : animatableManager.getAnimationControllers().values()) {
			if (this.reloadAnimations) {
				controller.forceAnimationReset();
				controller.getBoneAnimationQueues().clear();
			}

			controller.beginTick(animationState, this.bones, boneSnapshots, lerpedAnimationTick);

			for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values()) {
				GeoBone bone = boneAnimation.bone();
				BoneSnapshot snapshot = boneSnapshots.get(bone.getName());
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

				AnimationPoint rotXPoint = boneAnimation.rotationXQueue().poll();
				AnimationPoint rotYPoint = boneAnimation.rotationYQueue().poll();
				AnimationPoint rotZPoint = boneAnimation.rotationZQueue().poll();
				AnimationPoint posXPoint = boneAnimation.positionXQueue().poll();
				AnimationPoint posYPoint = boneAnimation.positionYQueue().poll();
				AnimationPoint posZPoint = boneAnimation.positionZQueue().poll();
				AnimationPoint scaleXPoint = boneAnimation.scaleXQueue().poll();
				AnimationPoint scaleYPoint = boneAnimation.scaleYQueue().poll();
				AnimationPoint scaleZPoint = boneAnimation.scaleZQueue().poll();
				EasingType easingType = controller.overrideEasingTypeFunction.apply(animationState);

				if (rotXPoint != null && rotYPoint != null && rotZPoint != null) {
					bone.setRotX((float)EasingType.lerpWithOverride(rotXPoint, easingType, animationState) + initialSnapshot.getRotX());
					bone.setRotY((float)EasingType.lerpWithOverride(rotYPoint, easingType, animationState) + initialSnapshot.getRotY());
					bone.setRotZ((float)EasingType.lerpWithOverride(rotZPoint, easingType, animationState) + initialSnapshot.getRotZ());
					snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
					snapshot.startRotAnim();
					bone.markRotationAsChanged();
				}

				if (posXPoint != null && posYPoint != null && posZPoint != null) {
					bone.setPosX((float)EasingType.lerpWithOverride(posXPoint, easingType, animationState));
					bone.setPosY((float)EasingType.lerpWithOverride(posYPoint, easingType, animationState));
					bone.setPosZ((float)EasingType.lerpWithOverride(posZPoint, easingType, animationState));
					snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
					snapshot.startPosAnim();
					bone.markPositionAsChanged();
				}

				if (scaleXPoint != null && scaleYPoint != null && scaleZPoint != null) {
					bone.setScaleX((float)EasingType.lerpWithOverride(scaleXPoint, easingType, animationState));
					bone.setScaleY((float)EasingType.lerpWithOverride(scaleYPoint, easingType, animationState));
					bone.setScaleZ((float)EasingType.lerpWithOverride(scaleZPoint, easingType, animationState));
					snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
					snapshot.startScaleAnim();
					bone.markScaleAsChanged();
				}
			}

			controller.finishRenderPass();
		}

		this.reloadAnimations = false;
		double resetTickLength = animationState.getData(DataTickets.BONE_RESET_TIME);

		for (GeoBone bone : getRegisteredBones()) {
			if (!bone.hasRotationChanged()) {
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
				BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

				if (saveSnapshot.isRotAnimInProgress())
					saveSnapshot.stopRotAnim(lerpedAnimationTick);

				double percentageReset = resetTickLength == 0 ? 1 : Math.min((lerpedAnimationTick - saveSnapshot.getLastResetRotationTick()) / resetTickLength, 1);
				float initialRotX = initialSnapshot.getRotX();
				float initialRotY = initialSnapshot.getRotY();
				float initialRotZ = initialSnapshot.getRotZ();
				float lastXRot = saveSnapshot.getRotX();
				float lastYRot = saveSnapshot.getRotY();
				float lastZRot = saveSnapshot.getRotZ();

				// Let's capture suspected full-rotations and prevent them from back-lerping
				// Far from perfect, but the best I can think of until I redo the system itself
				if (percentageReset == 0) {
					if (lastXRot != initialRotX && isSuspectedCompletedRotation(lastXRot)) {
						lastXRot = initialRotX;
						percentageReset = 1;
					}

					if (lastYRot != initialRotY && isSuspectedCompletedRotation(lastYRot)) {
						lastYRot = initialRotY;
						percentageReset = 1;
					}

					if (lastZRot != initialRotZ && isSuspectedCompletedRotation(lastZRot)) {
						lastZRot = initialRotZ;
						percentageReset = 1;
					}
				}

				bone.setRotX((float)Mth.lerp(percentageReset, lastXRot, initialRotX));
				bone.setRotY((float)Mth.lerp(percentageReset, lastYRot, initialRotY));
				bone.setRotZ((float)Mth.lerp(percentageReset, lastZRot, initialRotZ));

				if (percentageReset >= 1)
					saveSnapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
			}

			if (!bone.hasPositionChanged()) {
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
				BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

				if (saveSnapshot.isPosAnimInProgress())
					saveSnapshot.stopPosAnim(lerpedAnimationTick);

				double percentageReset = resetTickLength == 0 ? 1 : Math.min((lerpedAnimationTick - saveSnapshot.getLastResetPositionTick()) / resetTickLength, 1);

				bone.setPosX((float)Mth.lerp(percentageReset, saveSnapshot.getOffsetX(), initialSnapshot.getOffsetX()));
				bone.setPosY((float)Mth.lerp(percentageReset, saveSnapshot.getOffsetY(), initialSnapshot.getOffsetY()));
				bone.setPosZ((float)Mth.lerp(percentageReset, saveSnapshot.getOffsetZ(), initialSnapshot.getOffsetZ()));

				if (percentageReset >= 1)
					saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
			}

			if (!bone.hasScaleChanged()) {
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
				BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

				if (saveSnapshot.isScaleAnimInProgress())
					saveSnapshot.stopScaleAnim(lerpedAnimationTick);

				double percentageReset = resetTickLength == 0 ? 1 : Math.min((lerpedAnimationTick - saveSnapshot.getLastResetScaleTick()) / resetTickLength, 1);

				bone.setScaleX((float)Mth.lerp(percentageReset, saveSnapshot.getScaleX(), initialSnapshot.getScaleX()));
				bone.setScaleY((float)Mth.lerp(percentageReset, saveSnapshot.getScaleY(), initialSnapshot.getScaleY()));
				bone.setScaleZ((float)Mth.lerp(percentageReset, saveSnapshot.getScaleZ(), initialSnapshot.getScaleZ()));

				if (percentageReset >= 1)
					saveSnapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
			}
		}

		resetBoneTransformationMarkers();
		animatableManager.finishFirstTick();
	}

	/**
	 * Bandaid helper to try to detect suspected completed rotations in an animation frame
	 * <p>
	 * By no means perfectly accurate, but is the best we can do until the system is changed
	 */
	private boolean isSuspectedCompletedRotation(float lastRotation) {
		float rotations = Mth.abs(lastRotation / (360f * Mth.DEG_TO_RAD));
		float partialRotation = 1 - (rotations - (int)rotations);

		return partialRotation == 1 || partialRotation < 0.026 * rotations;
	}

	/**
	 * Reset the transformation markers applied to each {@link GeoBone} ready for the next render frame
	 */
	private void resetBoneTransformationMarkers() {
		getRegisteredBones().forEach(GeoBone::resetStateChanges);
	}

	/**
	 * Create new bone {@link BoneSnapshot} based on the bone's initial snapshot for the currently registered {@link GeoBone GeoBones},
	 * filtered by the bones already present in the master snapshots map
	 *
	 * @param snapshots The master bone snapshots map from the related {@link AnimatableManager}
	 * @return The input snapshots map, for easy assignment
	 */
	private Map<String, BoneSnapshot> updateBoneSnapshots(Map<String, BoneSnapshot> snapshots) {
		for (GeoBone bone : getRegisteredBones()) {
			if (!snapshots.containsKey(bone.getName()))
				snapshots.put(bone.getName(), BoneSnapshot.copy(bone.getInitialSnapshot()));
		}

		return snapshots;
	}

	/**
	 * Gets a bone by name
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	public GeoBone getBone(String boneName) {
		return this.bones.get(boneName);
	}

	/**
	 * Adds the given bone to the bones list for this processor
	 * <p>
	 * This is normally handled automatically by Geckolib
	 * <p>
	 * Failure to properly register a bone will break things.
	 */
	public void registerGeoBone(GeoBone bone) {
		bone.saveInitialSnapshot();
		this.bones.put(bone.getName(), bone);

		for (GeoBone child : bone.getChildBones()) {
			registerGeoBone(child);
		}
	}

	/**
	 * Clear the {@link GeoBone GeoBones} currently registered to the processor,
	 * then prepares the processor for a new model
	 * <p>
	 * Should be called whenever switching models to render/animate
	 */
	public void setActiveModel(BakedGeoModel model) {
		this.bones.clear();

		for (GeoBone bone : model.topLevelBones()) {
			registerGeoBone(bone);
		}
	}

	/**
	 * Get an iterable collection of the {@link GeoBone GeoBones} currently registered to the processor
	 */
	public Collection<GeoBone> getRegisteredBones() {
		return this.bones.values();
	}

	/**
	 * {@link Animation} and {@link Animation.LoopType} override pair,
	 * used to define a playable animation stage for a {@link GeoAnimatable}
	 */
	public record QueuedAnimation(Animation animation, Animation.LoopType loopType) {}
}
