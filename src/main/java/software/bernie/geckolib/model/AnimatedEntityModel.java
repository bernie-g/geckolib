package software.bernie.geckolib.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.util.JSONException;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.b3d.B3DModel;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.IAnimatedEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.file.AnimationFileManager;
import software.bernie.geckolib.json.JSONAnimationUtils;

import java.util.*;
import java.util.List;

public abstract class AnimatedEntityModel<T extends Entity & IAnimatedEntity> extends EntityModel<T>
{
	private JsonObject animationFile;
	private AnimationFileManager animationFileManager;
	private List<AnimatedModelRenderer> modelRendererList = new ArrayList();
	private HashMap<String, Animation> animationList = new HashMap();

	public abstract ResourceLocation getAnimationFileLocation();

	public boolean loopByDefault = false;

	public AnimatedEntityModel()
	{
		super();
		try
		{
			animationFileManager = new AnimationFileManager(getAnimationFileLocation());
			setAnimationFile(animationFileManager.loadAnimationFile());
			loadAllAnimations();
		}
		catch (Exception e)
		{
			GeckoLib.LOGGER.error("Encountered error while loading initial animations.", e);
		}
	}

	private void loadAllAnimations()
	{
		Set<Map.Entry<String, JsonElement>> entrySet = JSONAnimationUtils.getAnimations(getAnimationFile());
		for (Map.Entry<String, JsonElement> entry : entrySet)
		{
			String animationName = entry.getKey();
			Animation animation = null;
			try
			{
				animation = JSONAnimationUtils.deserializeJsonToAnimation(
						JSONAnimationUtils.getAnimation(getAnimationFile(), animationName));
				if (loopByDefault)
				{
					animation.loop = true;
				}
			}
			catch (JSONException e)
			{
				GeckoLib.LOGGER.error("Could not load animation: " + animationName, e);
			}
			animationList.put(animationName, animation);
		}
	}

	public JsonObject getAnimationFile()
	{
		return animationFile;
	}

	public void setAnimationFile(JsonObject animationFile)
	{
		this.animationFile = animationFile;
	}

	public AnimationFileManager getAnimationFileManager()
	{
		return animationFileManager;
	}


	public AnimatedModelRenderer getBone(String boneName)
	{
		return modelRendererList.stream().filter(x -> x.name.equals(boneName)).findFirst().orElse(
				null);
	}

	public void registerModelRenderer(AnimatedModelRenderer modelRenderer)
	{
		modelRenderer.saveInitialSnapshot();
		modelRendererList.add(modelRenderer);
	}

	public Map.Entry<String, JsonElement> getAnimationByName(String name) throws JSONException
	{
		return JSONAnimationUtils.getAnimation(getAnimationFile(), name);
	}

	public void setRotationAngle(AnimatedModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
	{
		// Keeps track of which bones have had animations applied to them, and eventually sets the ones that don't have an animation to their default values
		EntityDirtyTracker modelTracker = createNewDirtyTracker();

		// Adding partial tick to smooth out the animation
		double tick = entity.ticksExisted + partialTick;

		// Each animation has it's own collection of animations (called the AnimationControllerCollection), which allows for multiple independent animations
		AnimationControllerCollection controllers = entity.getAnimationControllers();

		// Store the current value of each bone rotation/position/scale
		if(controllers.boneSnapshotCollection.isEmpty())
		{
			controllers.boneSnapshotCollection = createNewBoneSnapshotCollection();
		}
		BoneSnapshotCollection boneSnapshots = controllers.boneSnapshotCollection;

		for (AnimationController<T> controller : controllers.values())
		{

			AnimationTestEvent<T> animationTestEvent = new AnimationTestEvent<T>(entity, tick, limbSwing, limbSwingAmount, partialTick, controller);

			// Process animations and add new values to the point queues
			controller.process(tick, animationTestEvent, modelRendererList);

			// Loop through every single bone and lerp each property
			for (BoneAnimationQueue boneAnimation : controller.boneAnimationQueues.values())
			{
				AnimatedModelRenderer bone = boneAnimation.bone;
				BoneSnapshot snapshot = boneSnapshots.get(bone.name);
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
				if (rXPoint != null && rYPoint != null && rZPoint != null)
				{
					bone.rotateAngleX = AnimationUtils.lerpValues(rXPoint) + initialSnapshot.rotationValueX;
					bone.rotateAngleY = AnimationUtils.lerpValues(rYPoint) + initialSnapshot.rotationValueY;
					bone.rotateAngleZ = AnimationUtils.lerpValues(rZPoint) + initialSnapshot.rotationValueZ;
					snapshot.rotationValueX = bone.rotateAngleX;
					snapshot.rotationValueY = bone.rotateAngleY;
					snapshot.rotationValueZ = bone.rotateAngleZ;

					modelTracker.get(bone).hasRotationChanged = true;
				}
				// If there's any position points for this bone
				if (pXPoint != null && pYPoint != null && pZPoint != null)
				{
					bone.positionOffsetX = AnimationUtils.lerpValues(pXPoint);
					bone.positionOffsetY = AnimationUtils.lerpValues(pYPoint);
					bone.positionOffsetZ = AnimationUtils.lerpValues(pZPoint);
					snapshot.positionOffsetX = bone.positionOffsetX;
					snapshot.positionOffsetY = bone.positionOffsetY;
					snapshot.positionOffsetZ = bone.positionOffsetZ;
					modelTracker.get(bone).hasPositionChanged = true;
				}

				// If there's any scale points for this bone
				if (sXPoint != null && sYPoint != null && sZPoint != null)
				{
					bone.scaleValueX = AnimationUtils.lerpValues(sXPoint);
					bone.scaleValueY = AnimationUtils.lerpValues(sYPoint);
					bone.scaleValueZ = AnimationUtils.lerpValues(sZPoint);
					snapshot.scaleValueX = bone.scaleValueX;
					snapshot.scaleValueY = bone.scaleValueY;
					snapshot.scaleValueZ = bone.scaleValueZ;
					modelTracker.get(bone).hasScaleChanged = true;
				}
			}
		}

		for (DirtyTracker tracker : modelTracker)
		{
			AnimatedModelRenderer model = tracker.model;
			BoneSnapshot snapshot = model.getInitialSnapshot();
			BoneSnapshot currentSnapshot = boneSnapshots.get(model.name);
			if (!tracker.hasRotationChanged)
			{
				if(tracker.model.name.equals("larm1"))
				{
					GeckoLib.LOGGER.info(model.rotateAngleX);
				}
				model.rotateAngleX = lerpConstant(model.rotateAngleX, snapshot.rotationValueX, 0.02);
				model.rotateAngleY = lerpConstant(model.rotateAngleY, snapshot.rotationValueY, 0.02);
				model.rotateAngleZ = lerpConstant(model.rotateAngleZ, snapshot.rotationValueZ, 0.02);
			}
			if (!tracker.hasPositionChanged)
			{
				model.positionOffsetX = lerpConstant(model.positionOffsetX, snapshot.positionOffsetX, 0.01);
				model.positionOffsetY = lerpConstant(model.positionOffsetY, snapshot.positionOffsetY, 0.01);
				model.positionOffsetZ = lerpConstant(model.positionOffsetZ, snapshot.positionOffsetZ, 0.01);
			}
			if (!tracker.hasScaleChanged)
			{
				model.scaleValueX = lerpConstant(model.scaleValueX, snapshot.scaleValueX, 0.05);
				model.scaleValueY = lerpConstant(model.scaleValueY, snapshot.scaleValueY, 0.05);
				model.scaleValueZ = lerpConstant(model.scaleValueZ, snapshot.scaleValueZ, 0.05);
			}
		}
	}

	private EntityDirtyTracker createNewDirtyTracker()
	{
		EntityDirtyTracker tracker = new EntityDirtyTracker();
		for(AnimatedModelRenderer bone : modelRendererList)
		{
			tracker.add(new DirtyTracker(false, false, false, bone));
		}
		return tracker;
	}

	private BoneSnapshotCollection createNewBoneSnapshotCollection()
	{
		BoneSnapshotCollection collection = new BoneSnapshotCollection();
		for(AnimatedModelRenderer bone : modelRendererList)
		{
			collection.put(bone.name, new BoneSnapshot(bone.getInitialSnapshot()));
		}
		return collection;
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{

	}

	public Animation getAnimation(String name)
	{
		return animationList.get(name);
	}

	private static float lerpConstant(double currentValue, double finalValue, double speedModifier)
	{
		double lowerBound = finalValue - speedModifier;
		double upperBound = finalValue + speedModifier;

		if(lowerBound <= currentValue && upperBound >= currentValue)
		{
			return (float) currentValue;
		}
		double increment = 0;
		if(currentValue < finalValue)
		{
			increment = speedModifier;
		}
		else {
			increment = -1 * speedModifier;
		}

		return (float) (currentValue + increment);
	}
}
