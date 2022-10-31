package software.bernie.geckolib3.core.processor;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;
import software.bernie.geckolib3.core.keyframe.BoneAnimationQueue;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.core.snapshot.DirtyTracker;
import software.bernie.geckolib3.core.util.MathUtil;

import java.util.List;
import java.util.Map;

public class AnimationProcessor<T extends IAnimatable> {
	public boolean reloadAnimations = false;
	private final List<IBone> modelRendererList = new ObjectArrayList<>();
	private double lastTickValue = -1;
	private final IntSet animatedEntities = new IntOpenHashSet();
	private final IAnimatableModel animatedModel;

	public AnimationProcessor(IAnimatableModel animatedModel) {
		this.animatedModel = animatedModel;
	}

	public void tickAnimation(IAnimatable entity, int uniqueID, double seekTime, AnimationEvent<T> event,
			MolangParser parser, boolean crashWhenCantFindBone) {
		if (seekTime != lastTickValue) {
			animatedEntities.clear();
		} else if (animatedEntities.contains(uniqueID)) { // Entity already animated on this tick
			return;
		}

		lastTickValue = seekTime;
		animatedEntities.add(uniqueID);

		// Each animation has its own collection of animations (called the
		// EntityAnimationManager), which allows for multiple independent animations
		AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);
		// Keeps track of which bones have had animations applied to them, and
		// eventually sets the ones that don't have an animation to their default values
		Map<String, DirtyTracker> modelTracker = createNewDirtyTracker();

		// Store the current value of each bone rotation/position/scale
		updateBoneSnapshots(manager.getBoneSnapshotCollection());

		Map<String, Pair<IBone, BoneSnapshot>> boneSnapshots = manager.getBoneSnapshotCollection();

		for (AnimationController<T> controller : manager.getAnimationControllers().values()) {
			if (reloadAnimations) {
				controller.markNeedsReload();
				controller.getBoneAnimationQueues().clear();
			}

			controller.isJustStarting = manager.isFirstTick;

			// Set current controller to animation test event
			event.setController(controller);

			// Process animations and add new values to the point queues
			controller.process(seekTime, event, modelRendererList, boneSnapshots, parser, crashWhenCantFindBone);

			// Loop through every single bone and lerp each property
			for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values()) {
				IBone bone = boneAnimation.bone();
				BoneSnapshot snapshot = boneSnapshots.get(bone.getName()).getRight();
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

				AnimationPoint rXPoint = boneAnimation.rotationXQueue().poll();
				AnimationPoint rYPoint = boneAnimation.rotationYQueue().poll();
				AnimationPoint rZPoint = boneAnimation.rotationZQueue().poll();

				AnimationPoint pXPoint = boneAnimation.positionXQueue().poll();
				AnimationPoint pYPoint = boneAnimation.positionYQueue().poll();
				AnimationPoint pZPoint = boneAnimation.positionZQueue().poll();

				AnimationPoint sXPoint = boneAnimation.scaleXQueue().poll();
				AnimationPoint sYPoint = boneAnimation.scaleYQueue().poll();
				AnimationPoint sZPoint = boneAnimation.scaleZQueue().poll();

				// If there's any rotation points for this bone
				DirtyTracker dirtyTracker = modelTracker.get(bone.getName());
				if (dirtyTracker == null) {
					continue;
				}
				if (rXPoint != null && rYPoint != null && rZPoint != null) {
					bone.setRotationX(MathUtil.lerpValues(rXPoint, controller.easingType, controller.customEasingMethod)
							+ initialSnapshot.rotationValueX);
					bone.setRotationY(MathUtil.lerpValues(rYPoint, controller.easingType, controller.customEasingMethod)
							+ initialSnapshot.rotationValueY);
					bone.setRotationZ(MathUtil.lerpValues(rZPoint, controller.easingType, controller.customEasingMethod)
							+ initialSnapshot.rotationValueZ);
					snapshot.rotationValueX = bone.getRotationX();
					snapshot.rotationValueY = bone.getRotationY();
					snapshot.rotationValueZ = bone.getRotationZ();
					snapshot.isCurrentlyRunningRotationAnimation = true;
					dirtyTracker.hasRotationChanged = true;
				}

				// If there's any position points for this bone
				if (pXPoint != null && pYPoint != null && pZPoint != null) {
					bone.setPositionX(
							MathUtil.lerpValues(pXPoint, controller.easingType, controller.customEasingMethod));
					bone.setPositionY(
							MathUtil.lerpValues(pYPoint, controller.easingType, controller.customEasingMethod));
					bone.setPositionZ(
							MathUtil.lerpValues(pZPoint, controller.easingType, controller.customEasingMethod));
					snapshot.positionOffsetX = bone.getPositionX();
					snapshot.positionOffsetY = bone.getPositionY();
					snapshot.positionOffsetZ = bone.getPositionZ();
					snapshot.isCurrentlyRunningPositionAnimation = true;

					dirtyTracker.hasPositionChanged = true;
				}

				// If there's any scale points for this bone
				if (sXPoint != null && sYPoint != null && sZPoint != null) {
					bone.setScaleX(MathUtil.lerpValues(sXPoint, controller.easingType, controller.customEasingMethod));
					bone.setScaleY(MathUtil.lerpValues(sYPoint, controller.easingType, controller.customEasingMethod));
					bone.setScaleZ(MathUtil.lerpValues(sZPoint, controller.easingType, controller.customEasingMethod));
					snapshot.scaleValueX = bone.getScaleX();
					snapshot.scaleValueY = bone.getScaleY();
					snapshot.scaleValueZ = bone.getScaleZ();
					snapshot.isCurrentlyRunningScaleAnimation = true;

					dirtyTracker.hasScaleChanged = true;
				}
			}
		}

		this.reloadAnimations = false;

		double resetTickLength = manager.getResetSpeed();
		for (Map.Entry<String, DirtyTracker> tracker : modelTracker.entrySet()) {
			IBone model = tracker.getValue().model;
			BoneSnapshot initialSnapshot = model.getInitialSnapshot();
			BoneSnapshot saveSnapshot = boneSnapshots.get(tracker.getKey()).getRight();
			if (saveSnapshot == null) {
				if (crashWhenCantFindBone) {
					throw new RuntimeException(
							"Could not find save snapshot for bone: " + tracker.getValue().model.getName()
									+ ". Please don't add bones that are used in an animation at runtime.");
				} else {
					continue;
				}
			}

			if (!tracker.getValue().hasRotationChanged) {
				if (saveSnapshot.isCurrentlyRunningRotationAnimation) {
					saveSnapshot.mostRecentResetRotationTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningRotationAnimation = false;
				}

				double percentageReset = Math
						.min((seekTime - saveSnapshot.mostRecentResetRotationTick) / resetTickLength, 1);

				model.setRotationX(MathUtil.lerpValues(percentageReset, saveSnapshot.rotationValueX,
						initialSnapshot.rotationValueX));
				model.setRotationY(MathUtil.lerpValues(percentageReset, saveSnapshot.rotationValueY,
						initialSnapshot.rotationValueY));
				model.setRotationZ(MathUtil.lerpValues(percentageReset, saveSnapshot.rotationValueZ,
						initialSnapshot.rotationValueZ));

				if (percentageReset >= 1) {
					saveSnapshot.rotationValueX = model.getRotationX();
					saveSnapshot.rotationValueY = model.getRotationY();
					saveSnapshot.rotationValueZ = model.getRotationZ();
				}
			}
			if (!tracker.getValue().hasPositionChanged) {
				if (saveSnapshot.isCurrentlyRunningPositionAnimation) {
					saveSnapshot.mostRecentResetPositionTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningPositionAnimation = false;
				}

				double percentageReset = Math
						.min((seekTime - saveSnapshot.mostRecentResetPositionTick) / resetTickLength, 1);

				model.setPositionX(MathUtil.lerpValues(percentageReset, saveSnapshot.positionOffsetX,
						initialSnapshot.positionOffsetX));
				model.setPositionY(MathUtil.lerpValues(percentageReset, saveSnapshot.positionOffsetY,
						initialSnapshot.positionOffsetY));
				model.setPositionZ(MathUtil.lerpValues(percentageReset, saveSnapshot.positionOffsetZ,
						initialSnapshot.positionOffsetZ));

				if (percentageReset >= 1) {
					saveSnapshot.positionOffsetX = model.getPositionX();
					saveSnapshot.positionOffsetY = model.getPositionY();
					saveSnapshot.positionOffsetZ = model.getPositionZ();
				}
			}
			if (!tracker.getValue().hasScaleChanged) {
				if (saveSnapshot.isCurrentlyRunningScaleAnimation) {
					saveSnapshot.mostRecentResetScaleTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningScaleAnimation = false;
				}

				double percentageReset = Math.min((seekTime - saveSnapshot.mostRecentResetScaleTick) / resetTickLength,
						1);

				model.setScaleX(
						MathUtil.lerpValues(percentageReset, saveSnapshot.scaleValueX, initialSnapshot.scaleValueX));
				model.setScaleY(
						MathUtil.lerpValues(percentageReset, saveSnapshot.scaleValueY, initialSnapshot.scaleValueY));
				model.setScaleZ(
						MathUtil.lerpValues(percentageReset, saveSnapshot.scaleValueZ, initialSnapshot.scaleValueZ));

				if (percentageReset >= 1) {
					saveSnapshot.scaleValueX = model.getScaleX();
					saveSnapshot.scaleValueY = model.getScaleY();
					saveSnapshot.scaleValueZ = model.getScaleZ();
				}
			}
		}
		manager.isFirstTick = false;
	}

	private Map<String, DirtyTracker> createNewDirtyTracker() {
		Map<String, DirtyTracker> tracker = new Object2ObjectOpenHashMap<>();
		for (IBone bone : modelRendererList) {
			tracker.put(bone.getName(), new DirtyTracker(false, false, false, bone));
		}
		return tracker;
	}

	private void updateBoneSnapshots(Map<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection) {
		for (IBone bone : modelRendererList) {
			if (!boneSnapshotCollection.containsKey(bone.getName())) {
				boneSnapshotCollection.put(bone.getName(), Pair.of(bone, new BoneSnapshot(bone.getInitialSnapshot())));
			}
		}
	}

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	public IBone getBone(String boneName) {
		for (IBone bone : this.modelRendererList) {
			if (bone.getName().equals(boneName))
				return bone;
		}

		return null;
	}

	/**
	 * Register model renderer. Each AnimatedModelRenderer (group in blockbench)
	 * NEEDS to be registered via this method.
	 *
	 * @param modelRenderer The model renderer
	 */
	public void registerModelRenderer(IBone modelRenderer) {
		modelRenderer.saveInitialSnapshot();
		modelRendererList.add(modelRenderer);
	}

	public void clearModelRendererList() {
		this.modelRendererList.clear();
	}

	public List<IBone> getModelRendererList() {
		return modelRendererList;
	}

	public void preAnimationSetup(IAnimatable animatable, double seekTime) {
		this.animatedModel.setMolangQueries(animatable, seekTime);
	}
}
