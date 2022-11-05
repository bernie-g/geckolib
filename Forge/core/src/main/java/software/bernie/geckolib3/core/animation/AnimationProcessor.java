package software.bernie.geckolib3.core.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animatable.model.GeoBone;
import software.bernie.geckolib3.core.animatable.model.GeoModel;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;
import software.bernie.geckolib3.core.keyframe.BoneAnimationQueue;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.state.BoneSnapshot;
import software.bernie.geckolib3.core.util.MathUtil;

import java.util.*;

public class AnimationProcessor<T extends GeoAnimatable> {
	private final Map<String, GeoBone> bones = new Object2ObjectOpenHashMap<>();
	private final GeoModel<T> model;

	public boolean reloadAnimations = false;

	public AnimationProcessor(GeoModel<T> model) {
		this.model = model;
	}

	/**
	 * Build an animation queue for the given {@link RawAnimation}
	 * @param animatable The animatable object being rendered
	 * @param rawAnimation The raw animation to be compiled
	 * @return A queue of animations and loop types to play
	 */
	public Queue<QueuedAnimation> buildAnimationQueue(T animatable, RawAnimation rawAnimation) {
		LinkedList<QueuedAnimation> animations = new LinkedList<>();
		boolean error = false;

		for (RawAnimation.Stage stage : rawAnimation.getAnimationStages()) {
			Animation animation = model.getAnimation(animatable, stage.animationName());

			if (animation == null) {
				System.out.printf("Could not load animation: %s. Is it missing?", stage.animationName());

				error = true;
			}
			else {
				animations.add(new QueuedAnimation(animation, stage.loopType()));
			}
		}

		return error ? null : animations;
	}

	/**
	 * Tick and apply transformations to the model based on the current state of the {@link AnimationController}
	 * @param animatable The animatable object relevant to the animation being played
	 * @param instanceId The {@code int} id for the instance being rendered
	 * @param seekTime The current lerped tick (current tick + partial tick)
	 * @param event An {@link AnimationEvent} instance applied to this render frame
	 * @param crashWhenCantFindBone Whether to crash if unable to find a required bone, or to continue with the remaining bones
	 */
	public void tickAnimation(T animatable, int instanceId, double seekTime, AnimationEvent<T> event, boolean crashWhenCantFindBone) {
		AnimationData manager = animatable.getFactory().getOrCreateAnimationData(instanceId);
		Map<String, BoneSnapshot> boneSnapshots = updateBoneSnapshots(manager.getBoneSnapshotCollection());
		List<GeoBone> modifiedBones = new ObjectArrayList<>();

		resetBoneTransformationMarkers();

		for (AnimationController<T> controller : manager.getAnimationControllers().values()) {
			if (this.reloadAnimations) {
				controller.markNeedsReload();
				controller.getBoneAnimationQueues().clear();
			}

			controller.isJustStarting = manager.isFirstTick;

			event.withController(controller);
			controller.process(seekTime, event, this.bones.values(), boneSnapshots, crashWhenCantFindBone);

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
				EasingType easingType = controller.easingTypeFunction.apply(animatable);

				if (rotXPoint != null && rotYPoint != null && rotZPoint != null) {
					bone.setRotX(MathUtil.lerpValues(rotXPoint, easingType) + initialSnapshot.getRotX());
					bone.setRotY(MathUtil.lerpValues(rotYPoint, easingType) + initialSnapshot.getRotY());
					bone.setRotZ(MathUtil.lerpValues(rotZPoint, easingType) + initialSnapshot.getRotZ());

					snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
					snapshot.startRotAnim();

					bone.markRotationAsChanged();
					modifiedBones.add(bone);
				}

				if (posXPoint != null && posYPoint != null && posZPoint != null) {
					bone.setPosX(
							MathUtil.lerpValues(posXPoint, easingType));
					bone.setPosY(
							MathUtil.lerpValues(posYPoint, easingType));
					bone.setPosZ(
							MathUtil.lerpValues(posZPoint, easingType));
					snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
					snapshot.startPosAnim();

					bone.markPositionAsChanged();
					modifiedBones.add(bone);
				}

				if (scaleXPoint != null && scaleYPoint != null && scaleZPoint != null) {
					bone.setScaleX(MathUtil.lerpValues(scaleXPoint, easingType));
					bone.setScaleY(MathUtil.lerpValues(scaleYPoint, easingType));
					bone.setScaleZ(MathUtil.lerpValues(scaleZPoint, easingType));
					snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
					snapshot.startScaleAnim();

					bone.markScaleAsChanged();
					modifiedBones.add(bone);
				}
			}
		}

		this.reloadAnimations = false;
		double resetTickLength = manager.getResetSpeed();

		for (GeoBone bone : modifiedBones) {
			BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
			BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

			if (saveSnapshot == null) {
				if (crashWhenCantFindBone) {
					throw new RuntimeException(
							"Could not find save snapshot for bone: " + bone.getName() + ". Please don't add bones that are used in an animation at runtime.");
				}
				else {
					continue;
				}
			}

			if (!bone.hasRotationChanged()) {
				if (saveSnapshot.isRotAnimInProgress())
					saveSnapshot.stopRotAnim(seekTime);

				double percentageReset = Math
						.min((seekTime - saveSnapshot.getLastResetRotationTick()) / resetTickLength, 1);

				bone.setRotX(MathUtil.lerpValues(percentageReset, saveSnapshot.getRotX(),
						initialSnapshot.getRotX()));
				bone.setRotY(MathUtil.lerpValues(percentageReset, saveSnapshot.getRotY(),
						initialSnapshot.getRotY()));
				bone.setRotZ(MathUtil.lerpValues(percentageReset, saveSnapshot.getRotZ(),
						initialSnapshot.getRotZ()));

				if (percentageReset >= 1)
					saveSnapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
			}

			if (!bone.hasPositionChanged()) {
				if (saveSnapshot.isPosAnimInProgress())
					saveSnapshot.stopPosAnim(seekTime);

				double percentageReset = Math
						.min((seekTime - saveSnapshot.getLastResetPositionTick()) / resetTickLength, 1);

				bone.setPosX(MathUtil.lerpValues(percentageReset, saveSnapshot.getOffsetX(),
						initialSnapshot.getOffsetX()));
				bone.setPosY(MathUtil.lerpValues(percentageReset, saveSnapshot.getOffsetY(),
						initialSnapshot.getOffsetY()));
				bone.setPosZ(MathUtil.lerpValues(percentageReset, saveSnapshot.getOffsetZ(),
						initialSnapshot.getOffsetZ()));

				if (percentageReset >= 1)
					saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
			}

			if (!bone.hasScaleChanged()) {
				if (saveSnapshot.isScaleAnimInProgress())
					saveSnapshot.stopScaleAnim(seekTime);

				double percentageReset = Math.min((seekTime - saveSnapshot.getLastResetScaleTick()) / resetTickLength,
						1);

				bone.setScaleX(MathUtil.lerpValues(percentageReset, saveSnapshot.getScaleX(), initialSnapshot.getScaleX()));
				bone.setScaleY(MathUtil.lerpValues(percentageReset, saveSnapshot.getScaleY(), initialSnapshot.getScaleY()));
				bone.setScaleZ(MathUtil.lerpValues(percentageReset, saveSnapshot.getScaleZ(), initialSnapshot.getScaleZ()));

				if (percentageReset >= 1)
					saveSnapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
			}
		}

		manager.isFirstTick = false;
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
	 * @param snapshots The master bone snapshots map from the related {@link AnimationData}
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
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	public GeoBone getBone(String boneName) {
		return this.bones.get(boneName);
	}

	/**
	 * Register model renderer. Each AnimatedModelRenderer (group in blockbench)
	 * NEEDS to be registered via this method.
	 *
	 * @param bone The model renderer
	 */
	public void registerModelRenderer(GeoBone bone) {
		bone.saveInitialSnapshot();
		bones.put(bone.getName(), bone);
	}

	/**
	 * Clear the {@link GeoBone GeoBones} currently registered to the processor,
	 * to prepare for a new collection of {@code GeoBones} to be registered
	 */
	public void clearModelRendererList() {
		this.bones.clear();
	}

	/**
	 * Get an iterable collection of the {@link GeoBone GeoBones} currently registered to the processor
	 */
	public Collection<GeoBone> getRegisteredBones() {
		return this.bones.values();
	}

	/**
	 * Apply transformations and settings prior to acting on any animation-related functionality
	 */
	public void preAnimationSetup(T animatable, double seekTime) {
		this.model.applyMolangQueries(animatable, seekTime);
	}

	/**
	 * {@link Animation} and {@link software.bernie.geckolib3.core.animation.Animation.LoopType} override pair,
	 * used to define a playable animation stage for a {@link software.bernie.geckolib3.core.animatable.GeoAnimatable}
	 */
	public record QueuedAnimation(Animation animation, Animation.LoopType loopType) {}
}
