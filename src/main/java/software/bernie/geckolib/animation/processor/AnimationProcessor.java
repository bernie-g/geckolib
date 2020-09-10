package software.bernie.geckolib.animation.processor;

import com.eliotlash.molang.MolangParser;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.snapshot.BoneSnapshot;
import software.bernie.geckolib.animation.snapshot.BoneSnapshotCollection;
import software.bernie.geckolib.animation.snapshot.DirtyTracker;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.util.AnimationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationProcessor<T extends IAnimatable>
{
	private List<IBone> modelRendererList = new ArrayList();

	public void tickAnimation(IAnimatable entity, double seekTime, AnimationTestPredicate predicate, MolangParser parser, boolean crashWhenCantFindBone)
	{
		// Each animation has it's own collection of animations (called the EntityAnimationManager), which allows for multiple independent animations
		AnimationManager manager = entity.getAnimationManager();
		// Keeps track of which bones have had animations applied to them, and eventually sets the ones that don't have an animation to their default values
		HashMap<String, DirtyTracker> modelTracker = createNewDirtyTracker();

		// Store the current value of each bone rotation/position/scale
		if (manager.getBoneSnapshotCollection().isEmpty())
		{
			manager.setBoneSnapshotCollection(createNewBoneSnapshotCollection());
		}
		BoneSnapshotCollection boneSnapshots = manager.getBoneSnapshotCollection();

		for (AnimationController<T> controller : manager.values())
		{
			controller.isJustStarting = manager.isFirstTick;

			// Set current controller to animation test event
			predicate.setController(controller);

			// Process animations and add new values to the point queues
			controller.process(seekTime, predicate, modelRendererList, boneSnapshots, parser, crashWhenCantFindBone);

			// Loop through every single bone and lerp each property
			for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values())
			{
				IBone bone = boneAnimation.bone;
				BoneSnapshot snapshot = boneSnapshots.get(bone.getName());
				BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

				AnimationPoint rXPoint = boneAnimation.rotationXQueue.poll();
				AnimationPoint rYPoint = boneAnimation.rotationYQueue.poll();
				AnimationPoint rZPoint = boneAnimation.rotationZQueue.poll();

				AnimationPoint pXPoint = boneAnimation.positionXQueue.poll();
				AnimationPoint pYPoint = boneAnimation.positionYQueue.poll();
				AnimationPoint pZPoint = boneAnimation.positionZQueue.poll();

				AnimationPoint sXPoint = boneAnimation.scaleXQueue.poll();
				AnimationPoint sYPoint = boneAnimation.scaleYQueue.poll();
				AnimationPoint sZPoint = boneAnimation.scaleZQueue.poll();

				// If there's any rotation points for this bone
				DirtyTracker dirtyTracker = modelTracker.get(bone.getName());
				if(dirtyTracker == null)
				{
					continue;
				}
				if (rXPoint != null && rYPoint != null && rZPoint != null)
				{
					bone.setRotationX(AnimationUtils.lerpValues(rXPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueX);
					bone.setRotationY(AnimationUtils.lerpValues(rYPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueY);
					bone.setRotationZ(AnimationUtils.lerpValues(rZPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueZ);
					snapshot.rotationValueX = bone.getRotationX();
					snapshot.rotationValueY = bone.getRotationY();
					snapshot.rotationValueZ = bone.getRotationZ();
					snapshot.isCurrentlyRunningRotationAnimation = true;

					dirtyTracker.hasRotationChanged = true;
				}

				// If there's any position points for this bone
				if (pXPoint != null && pYPoint != null && pZPoint != null)
				{
					bone.setPositionX(AnimationUtils.lerpValues(pXPoint, controller.easingType,
							controller.customEasingMethod));
					bone.setPositionY(AnimationUtils.lerpValues(pYPoint, controller.easingType,
							controller.customEasingMethod));
					bone.setPositionZ(AnimationUtils.lerpValues(pZPoint, controller.easingType,
							controller.customEasingMethod));
					snapshot.positionOffsetX = bone.getPositionX();
					snapshot.positionOffsetY = bone.getPositionY();
					snapshot.positionOffsetZ = bone.getPositionZ();
					snapshot.isCurrentlyRunningPositionAnimation = true;

					dirtyTracker.hasPositionChanged = true;
				}

				// If there's any scale points for this bone
				if (sXPoint != null && sYPoint != null && sZPoint != null)
				{
					bone.setScaleX(AnimationUtils.lerpValues(sXPoint, controller.easingType,
							controller.customEasingMethod));
					bone.setScaleY(AnimationUtils.lerpValues(sYPoint, controller.easingType,
							controller.customEasingMethod));
					bone.setScaleZ(AnimationUtils.lerpValues(sZPoint, controller.easingType,
							controller.customEasingMethod));
					snapshot.scaleValueX = bone.getScaleX();
					snapshot.scaleValueY = bone.getScaleY();
					snapshot.scaleValueZ = bone.getScaleZ();
					snapshot.isCurrentlyRunningScaleAnimation = true;

					dirtyTracker.hasScaleChanged = true;
				}
			}
		}

		double resetTickLength = manager.getResetSpeed();
		for (Map.Entry<String, DirtyTracker> tracker : modelTracker.entrySet())
		{
			IBone model = tracker.getValue().model;
			BoneSnapshot initialSnapshot = model.getInitialSnapshot();
			BoneSnapshot saveSnapshot = boneSnapshots.get(tracker.getValue().model.getName());
			if(saveSnapshot == null)
			{
				if(crashWhenCantFindBone)
				{
					throw new RuntimeException("Could not find save snapshot for bone: " + tracker.getValue().model.getName() + ". Please don't add bones that are used in an animation at runtime.");
				}
				else {
					continue;
				}
			}

			if (!tracker.getValue().hasRotationChanged)
			{
				if (saveSnapshot.isCurrentlyRunningRotationAnimation)
				{
					saveSnapshot.mostRecentResetRotationTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningRotationAnimation = false;
				}

				double percentageReset = Math.min((seekTime - saveSnapshot.mostRecentResetRotationTick) / resetTickLength, 1);

				model.setRotationX(AnimationUtils.lerpValues(percentageReset, saveSnapshot.rotationValueX,
						initialSnapshot.rotationValueX));
				model.setRotationY(AnimationUtils.lerpValues(percentageReset, saveSnapshot.rotationValueY,
						initialSnapshot.rotationValueY));
				model.setRotationZ(AnimationUtils.lerpValues(percentageReset, saveSnapshot.rotationValueZ,
						initialSnapshot.rotationValueZ));

				if(percentageReset >= 1)
				{
					saveSnapshot.rotationValueX = model.getRotationX();
					saveSnapshot.rotationValueY = model.getRotationY();
					saveSnapshot.rotationValueZ = model.getRotationZ();
				}
			}
			if (!tracker.getValue().hasPositionChanged)
			{
				if (saveSnapshot.isCurrentlyRunningPositionAnimation)
				{
					saveSnapshot.mostRecentResetPositionTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningPositionAnimation = false;
				}

				double percentageReset = Math.min((seekTime - saveSnapshot.mostRecentResetPositionTick) / resetTickLength, 1);

				model.setPositionX(AnimationUtils.lerpValues(percentageReset, saveSnapshot.positionOffsetX,
						initialSnapshot.positionOffsetX));
				model.setPositionY(AnimationUtils.lerpValues(percentageReset, saveSnapshot.positionOffsetY,
						initialSnapshot.positionOffsetY));
				model.setPositionZ(AnimationUtils.lerpValues(percentageReset, saveSnapshot.positionOffsetZ,
						initialSnapshot.positionOffsetZ));

				if(percentageReset >= 1)
				{
					saveSnapshot.positionOffsetX = model.getPositionX();
					saveSnapshot.positionOffsetY = model.getPositionY();
					saveSnapshot.positionOffsetZ = model.getPositionZ();
				}
			}
			if (!tracker.getValue().hasScaleChanged)
			{
				if (saveSnapshot.isCurrentlyRunningScaleAnimation)
				{
					saveSnapshot.mostRecentResetScaleTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningScaleAnimation = false;
				}

				double percentageReset = Math.min((seekTime - saveSnapshot.mostRecentResetScaleTick) / resetTickLength, 1);

				model.setScaleX(AnimationUtils.lerpValues(percentageReset, saveSnapshot.scaleValueX,
						initialSnapshot.scaleValueX));
				model.setScaleY(AnimationUtils.lerpValues(percentageReset, saveSnapshot.scaleValueY,
						initialSnapshot.scaleValueY));
				model.setScaleZ(AnimationUtils.lerpValues(percentageReset, saveSnapshot.scaleValueZ,
						initialSnapshot.scaleValueZ));

				if(percentageReset >= 1)
				{
					saveSnapshot.scaleValueX = model.getScaleX();
					saveSnapshot.scaleValueY = model.getScaleY();
					saveSnapshot.scaleValueZ = model.getScaleZ();
				}
			}
		}
		manager.isFirstTick = false;
	}

	private HashMap<String, DirtyTracker> createNewDirtyTracker()
	{
		HashMap<String, DirtyTracker> tracker = new HashMap<>();
		for (IBone bone : modelRendererList)
		{
			tracker.put(bone.getName(), new DirtyTracker(false, false, false, bone));
		}
		return tracker;
	}

	private BoneSnapshotCollection createNewBoneSnapshotCollection()
	{
		BoneSnapshotCollection collection = new BoneSnapshotCollection();
		for (IBone bone : modelRendererList)
		{
			collection.put(bone.getName(), new BoneSnapshot(bone.getInitialSnapshot()));
		}
		return collection;
	}

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	public IBone getBone(String boneName)
	{
		return modelRendererList.stream().filter(x -> x.getName().equals(boneName)).findFirst().orElse(
				null);
	}

	/**
	 * Register model renderer. Each AnimatedModelRenderer (group in blockbench) NEEDS to be registered via this method.
	 *
	 * @param modelRenderer The model renderer
	 */
	public void registerModelRenderer(IBone modelRenderer)
	{
		modelRenderer.saveInitialSnapshot();
		modelRendererList.add(modelRenderer);
	}


	public void clearModelRendererList()
	{
		this.modelRendererList.clear();
	}

	public List<IBone> getModelRendererList()
	{
		return modelRendererList;
	}
}
