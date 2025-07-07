package anightdazingzoroark.riftlib.core.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import anightdazingzoroark.riftlib.file.RidePosDefinitionList;
import anightdazingzoroark.riftlib.geo.render.built.GeoBone;
import anightdazingzoroark.riftlib.geo.render.built.GeoLocator;
import anightdazingzoroark.riftlib.hitboxLogic.EntityHitbox;
import anightdazingzoroark.riftlib.hitboxLogic.IMultiHitboxUser;
import anightdazingzoroark.riftlib.message.RiftLibMessage;
import anightdazingzoroark.riftlib.message.RiftLibUpdateHitboxPos;
import anightdazingzoroark.riftlib.message.RiftLibUpdateHitboxSize;
import anightdazingzoroark.riftlib.message.RiftLibUpdateRiderPos;
import anightdazingzoroark.riftlib.ridePositionLogic.DynamicRidePosUtils;
import anightdazingzoroark.riftlib.ridePositionLogic.IDynamicRideUser;
import anightdazingzoroark.riftlib.util.json.JsonHitboxUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;

import anightdazingzoroark.riftlib.molang.MolangParser;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.IAnimatableModel;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.event.predicate.AnimationEvent;
import anightdazingzoroark.riftlib.core.keyframe.AnimationPoint;
import anightdazingzoroark.riftlib.core.keyframe.BoneAnimationQueue;
import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.core.snapshot.BoneSnapshot;
import anightdazingzoroark.riftlib.core.snapshot.DirtyTracker;
import anightdazingzoroark.riftlib.core.util.MathUtil;

public class AnimationProcessor<T extends IAnimatable> {
	public boolean reloadAnimations = false;
	private List<IBone> modelRendererList = new ArrayList();
	private double lastTickValue = -1;
	private Set<Integer> animatedEntities = new HashSet<>();
	private final IAnimatableModel animatedModel;

	public AnimationProcessor(IAnimatableModel animatedModel) {
		this.animatedModel = animatedModel;
	}

	public void tickAnimation(IAnimatable entity, Integer uniqueID, double seekTime, AnimationEvent event,
			MolangParser parser, boolean crashWhenCantFindBone) {
		if (seekTime != lastTickValue) {
			this.animatedEntities.clear();
		}
		else if (this.animatedEntities.contains(uniqueID)) { // Entity already animated on this tick
			return;
		}

		this.lastTickValue = seekTime;
		this.animatedEntities.add(uniqueID);

		// Each animation has it's own collection of animations (called the
		// EntityAnimationManager), which allows for multiple independent animations
		AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);
		// Keeps track of which bones have had animations applied to them, and
		// eventually sets the ones that don't have an animation to their default values
		HashMap<String, DirtyTracker> modelTracker = createNewDirtyTracker();

		// Store the current value of each bone rotation/position/scale
		updateBoneSnapshots(manager.getBoneSnapshotCollection());

		HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshots = manager.getBoneSnapshotCollection();

		//create anim values list to store the changes in
		BoneAnimationValuesList boneAnimationValues = new BoneAnimationValuesList();

		//get changes from all anim controllers
		for (AnimationController<T> controller : manager.getAnimationControllers().values()) {
			if (this.reloadAnimations) {
				controller.markNeedsReload();
				controller.getBoneAnimationQueues().clear();
			}

			controller.isJustStarting = manager.isFirstTick;
			event.setController(controller);

			controller.process(seekTime, event, this.modelRendererList, boneSnapshots, parser, crashWhenCantFindBone);

			for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values()) {
				IBone bone = boneAnimation.bone;

				AnimationPoint rXPoint = boneAnimation.rotationXQueue.poll();
				AnimationPoint rYPoint = boneAnimation.rotationYQueue.poll();
				AnimationPoint rZPoint = boneAnimation.rotationZQueue.poll();

				AnimationPoint pXPoint = boneAnimation.positionXQueue.poll();
				AnimationPoint pYPoint = boneAnimation.positionYQueue.poll();
				AnimationPoint pZPoint = boneAnimation.positionZQueue.poll();

				AnimationPoint sXPoint = boneAnimation.scaleXQueue.poll();
				AnimationPoint sYPoint = boneAnimation.scaleYQueue.poll();
				AnimationPoint sZPoint = boneAnimation.scaleZQueue.poll();

				if (rXPoint != null && rYPoint != null && rZPoint != null) {
					boneAnimationValues.addRotations(
							bone.getName(),
							MathUtil.lerpValues(rXPoint, controller.easingType, controller.customEasingMethod),
							MathUtil.lerpValues(rYPoint, controller.easingType, controller.customEasingMethod),
							MathUtil.lerpValues(rZPoint, controller.easingType, controller.customEasingMethod)
					);
				}

				if (pXPoint != null && pYPoint != null && pZPoint != null) {
					boneAnimationValues.addPositions(
							bone.getName(),
							MathUtil.lerpValues(pXPoint, controller.easingType, controller.customEasingMethod),
							MathUtil.lerpValues(pYPoint, controller.easingType, controller.customEasingMethod),
							MathUtil.lerpValues(pZPoint, controller.easingType, controller.customEasingMethod)
					);
				}

				if (sXPoint != null && sYPoint != null && sZPoint != null) {
					boneAnimationValues.addScales(
							bone.getName(),
							MathUtil.lerpValues(sXPoint, controller.easingType, controller.customEasingMethod),
							MathUtil.lerpValues(sYPoint, controller.easingType, controller.customEasingMethod),
							MathUtil.lerpValues(sZPoint, controller.easingType, controller.customEasingMethod)
					);
				}


			}
		}

		//make a rideposdef list for changine ride positions
		RidePosDefinitionList definitionList = new RidePosDefinitionList();

		//apply changes from anims to bones, hitboxes, and rideposdeflist
		for (IBone bone : this.modelRendererList) {
			BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
			BoneSnapshot snapshot = boneSnapshots.get(bone.getName()).getRight();

			DirtyTracker dirtyTracker = modelTracker.get(bone.getName());
			if (dirtyTracker == null) continue;

			float[] rot = boneAnimationValues.getRotations(bone.getName());
			float[] pos = boneAnimationValues.getPositions(bone.getName());
			float[] scale = boneAnimationValues.getScales(bone.getName());

			bone.setRotationX(rot[0] + initialSnapshot.rotationValueX);
			bone.setRotationY(rot[1] + initialSnapshot.rotationValueY);
			bone.setRotationZ(rot[2] + initialSnapshot.rotationValueZ);

			bone.setPositionX(pos[0] + initialSnapshot.positionOffsetX);
			bone.setPositionY(pos[1] + initialSnapshot.positionOffsetY);
			bone.setPositionZ(pos[2] + initialSnapshot.positionOffsetZ);

			bone.setScaleX(scale[0] * initialSnapshot.scaleValueX);
			bone.setScaleY(scale[1] * initialSnapshot.scaleValueY);
			bone.setScaleZ(scale[2] * initialSnapshot.scaleValueZ);

			snapshot.rotationValueX = bone.getRotationX();
			snapshot.rotationValueY = bone.getRotationY();
			snapshot.rotationValueZ = bone.getRotationZ();
			snapshot.positionOffsetX = bone.getPositionX();
			snapshot.positionOffsetY = bone.getPositionY();
			snapshot.positionOffsetZ = bone.getPositionZ();
			snapshot.scaleValueX = bone.getScaleX();
			snapshot.scaleValueY = bone.getScaleY();
			snapshot.scaleValueZ = bone.getScaleZ();

			snapshot.isCurrentlyRunningRotationAnimation = true;
			snapshot.isCurrentlyRunningPositionAnimation = true;
			snapshot.isCurrentlyRunningScaleAnimation = true;

			dirtyTracker.hasRotationChanged = true;
			dirtyTracker.hasPositionChanged = true;
			dirtyTracker.hasScaleChanged = true;

			//apply via packets the new positions and sizes of the hitboxes
			if (entity instanceof IMultiHitboxUser) {
				GeoBone geoBone = (GeoBone) bone;
				for (GeoLocator locator : geoBone.childLocators) {
					if (JsonHitboxUtils.locatorCanBeHitbox(locator.name)) {
						String hitboxName = JsonHitboxUtils.locatorHitboxToHitbox(locator.name);

						//get hitbox associated with the locator
						EntityHitbox hitbox = ((IMultiHitboxUser) entity).getHitboxByName(hitboxName);

						//skip when hitbox is set to not be affected by animation
						if (!hitbox.affectedByAnim) continue;

						//packets for hitbox updates will not be sent if their total change
						//is too miniscule
						//get positions
						float newHitboxX = locator.positionX + (float) locator.getOffsetFromRotations().x + (float) locator.getOffsetFromDisplacements().x;
						float newHitboxY = locator.positionY + (float) locator.getOffsetFromRotations().y + (float) locator.getOffsetFromDisplacements().y - (hitbox.initHeight / 2f) - (bone.getScaleY() - 1) / 3;
						float newHitboxZ = -locator.positionZ - (float) locator.getOffsetFromRotations().z - (float) locator.getOffsetFromDisplacements().z;

						//get magnitude of displacement
						double dPosTotal = Math.sqrt(Math.pow(newHitboxX - hitbox.posX, 2) + Math.pow(newHitboxY - hitbox.posY, 2) + Math.pow(newHitboxZ - hitbox.posZ, 2));

						//update positions
						if (dPosTotal > 0.1) {
							RiftLibMessage.WRAPPER.sendToAll(new RiftLibUpdateHitboxPos(
									(Entity) entity,
									hitboxName,
									newHitboxX,
									newHitboxY,
									newHitboxZ
							));
							RiftLibMessage.WRAPPER.sendToServer(new RiftLibUpdateHitboxPos(
									(Entity) entity,
									hitboxName,
									newHitboxX,
									newHitboxY,
									newHitboxZ
							));
						}

						//get sizes
						float newHitboxWidth = Math.max(bone.getScaleX(), bone.getScaleZ());
						float newHitboxHeight = bone.getScaleY();

						//get magnitude of resizing
						double dSizeTotal = Math.sqrt(Math.pow(newHitboxWidth - hitbox.width, 2) + Math.pow(newHitboxHeight - hitbox.height, 2));

						//update sizes
						if (dSizeTotal > 0.1) {
							RiftLibMessage.WRAPPER.sendToAll(new RiftLibUpdateHitboxSize(
									(Entity) entity,
									hitboxName,
									Math.max(bone.getScaleX(), bone.getScaleZ()),
									bone.getScaleY()
							));
							RiftLibMessage.WRAPPER.sendToServer(new RiftLibUpdateHitboxSize(
									(Entity) entity,
									hitboxName,
									Math.max(bone.getScaleX(), bone.getScaleZ()),
									bone.getScaleY()
							));
						}
					}
				}
			}

			//apply via packets the new positions of the ride positions
			if (entity instanceof IDynamicRideUser) {
				GeoBone geoBone = (GeoBone) bone;
				//make a definition list and put in it the new positions based on the new locators positions
				for (GeoLocator locator : geoBone.childLocators) {
					if (DynamicRidePosUtils.locatorCanBeRidePos(locator.name)) {
						int ridePosIndex = DynamicRidePosUtils.locatorRideIndex(locator.name);
						definitionList.map.put(
								ridePosIndex,
								new Vec3d(
										locator.positionX + (float) locator.getOffsetFromRotations().x + (float) locator.getOffsetFromDisplacements().x,
										locator.positionY + (float) locator.getOffsetFromRotations().y + (float) locator.getOffsetFromDisplacements().y,
										-locator.positionZ - (float) locator.getOffsetFromRotations().z - (float) locator.getOffsetFromDisplacements().z
								)
						);
					}
				}
			}
		}

		//apply changes to ride positions
		if (entity instanceof IDynamicRideUser) {
			if (!definitionList.map.isEmpty()) {
				for (int x = 0; x < definitionList.finalOrderedRiderPositions().size(); x++) {
					//packets for ride position updates will not be sent if
					//their total change is too miniscule
					//get displacements
					double rXDisp = definitionList.finalOrderedRiderPositions().get(x).x - ((IDynamicRideUser) entity).ridePositions().get(x).x;
					double rYDisp = definitionList.finalOrderedRiderPositions().get(x).y - ((IDynamicRideUser) entity).ridePositions().get(x).y;
					double rZDisp = definitionList.finalOrderedRiderPositions().get(x).z - ((IDynamicRideUser) entity).ridePositions().get(x).z;

					//get magnitude of displacement
					double rDispTotal = Math.sqrt(rXDisp * rXDisp + rYDisp * rYDisp + rZDisp * rZDisp);

					//update ride positions
					if (rDispTotal > 0.05) {
						RiftLibMessage.WRAPPER.sendToAll(new RiftLibUpdateRiderPos(
								(Entity) entity,
								x,
								definitionList.finalOrderedRiderPositions().get(x)
						));
						RiftLibMessage.WRAPPER.sendToServer(new RiftLibUpdateRiderPos(
								(Entity) entity,
								x,
								definitionList.finalOrderedRiderPositions().get(x)
						));
					}
				}
			}
		}

		this.reloadAnimations = false;

		double resetTickLength = manager.getResetSpeed();
		BoneAnimationValuesList dBoneAnimationValues = new BoneAnimationValuesList();
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

				dBoneAnimationValues.addRotations(
						model.getName(),
						MathUtil.lerpValues(percentageReset, saveSnapshot.rotationValueX, initialSnapshot.rotationValueX),
						MathUtil.lerpValues(percentageReset, saveSnapshot.rotationValueY, initialSnapshot.rotationValueY),
						MathUtil.lerpValues(percentageReset, saveSnapshot.rotationValueZ, initialSnapshot.rotationValueZ)
				);
				model.setRotationX(dBoneAnimationValues.getRotations(model.getName())[0]);
				model.setRotationY(dBoneAnimationValues.getRotations(model.getName())[1]);
				model.setRotationZ(dBoneAnimationValues.getRotations(model.getName())[2]);

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

				dBoneAnimationValues.addPositions(
						model.getName(),
						MathUtil.lerpValues(percentageReset, saveSnapshot.positionOffsetX, initialSnapshot.positionOffsetX),
						MathUtil.lerpValues(percentageReset, saveSnapshot.positionOffsetY, initialSnapshot.positionOffsetY),
						MathUtil.lerpValues(percentageReset, saveSnapshot.positionOffsetZ, initialSnapshot.positionOffsetZ)
				);
				model.setPositionX(dBoneAnimationValues.getPositions(model.getName())[0]);
				model.setPositionY(dBoneAnimationValues.getPositions(model.getName())[1]);
				model.setPositionZ(dBoneAnimationValues.getPositions(model.getName())[2]);

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

	private HashMap<String, DirtyTracker> createNewDirtyTracker() {
		HashMap<String, DirtyTracker> tracker = new HashMap<>();
		for (IBone bone : modelRendererList) {
			tracker.put(bone.getName(), new DirtyTracker(false, false, false, bone));
		}
		return tracker;
	}

	private void updateBoneSnapshots(HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection) {
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
		return modelRendererList.stream().filter(x -> x.getName().equals(boneName)).findFirst().orElse(null);
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
