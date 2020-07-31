/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.util.JsonException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.animation.snapshot.BoneSnapshot;
import software.bernie.geckolib.animation.snapshot.BoneSnapshotCollection;
import software.bernie.geckolib.animation.snapshot.DirtyTracker;
import software.bernie.geckolib.animation.snapshot.EntityDirtyTracker;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.manager.EntityAnimationManager;
import software.bernie.geckolib.reload.ReloadManager;
import software.bernie.geckolib.util.AnimationUtils;
import software.bernie.geckolib.util.json.JsonAnimationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * An AnimatedEntityModel is the equivalent of an Entity Model, except it provides extra functionality for rendering animations from bedrock json animation files. The entity passed into the generic parameter needs to implement IAnimatedEntity.
 *
 * @param <T> the type parameter
 */
public abstract class AnimatedEntityModel<T extends EntityLivingBase & IAnimatedEntity> extends ModelBase implements IResourceManagerReloadListener
{
	private JsonObject animationFile;
	private List<AnimatedModelRenderer> modelRendererList = new ArrayList();
	private HashMap<String, Animation> animationList = new HashMap();
	public List<AnimatedModelRenderer> rootBones = new ArrayList<>();
	public double seekTime;
	public double lastGameTickTime;

	/**
	 * This resource location needs to point to a json file of your animation file, i.e. "geckolib:animations/frog_animation.json"
	 *
	 * @return the animation file location
	 */
	public abstract ResourceLocation getAnimationFileLocation();

	/**
	 * If animations should loop by default and ignore their pre-existing loop settings (that you can enable in blockbench by right clicking)
	 */
	public boolean loopByDefault = false;


	/**
	 * Instantiates a new Animated entity model and loads the current animation file.
	 */
	protected AnimatedEntityModel()
	{
		super();
		ReloadManager.registerModel(this);
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		//resourceManager.addReloadListener(this);
		onResourceManagerReload(resourceManager);
	}


	/**
	 * Internal method for handling reloads of animation files. Do not override.
	 */
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		try
		{
			Gson GSON = new Gson();
			SimpleResource resource = (SimpleResource) resourceManager.getResource(getAnimationFileLocation());
			InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
			Reader reader = new BufferedReader(
					stream);
			JsonObject jsonobject = JsonUtils.fromJson(GSON, reader, JsonObject.class);
			resource.close();
			stream.close();
			setAnimationFile(jsonobject);
			loadAllAnimations();
		}
		catch (IOException e)
		{
			GeckoLib.LOGGER.error("Encountered error while loading animations.", e);
			throw new RuntimeException(e);
		}
	}

	private void loadAllAnimations()
	{
		animationList.clear();
		Set<Map.Entry<String, JsonElement>> entrySet = JsonAnimationUtils.getAnimations(getAnimationFile());
		for (Map.Entry<String, JsonElement> entry : entrySet)
		{
			String animationName = entry.getKey();
			Animation animation = null;
			try
			{
				animation = JsonAnimationUtils.deserializeJsonToAnimation(
						JsonAnimationUtils.getAnimation(getAnimationFile(), animationName));
				if (loopByDefault)
				{
					animation.loop = true;
				}
			}
			catch (JsonException e)
			{
				GeckoLib.LOGGER.error("Could not load animation: " + animationName, e);
				throw new RuntimeException(e);
			}
			animationList.put(animationName, animation);
		}
	}

	/**
	 * Gets the current animation file.
	 *
	 * @return the animation file
	 */
	public JsonObject getAnimationFile()
	{
		return animationFile;
	}

	/**
	 * Sets the animation file to read from.
	 *
	 * @param animationFile The animation file
	 */
	public void setAnimationFile(JsonObject animationFile)
	{
		this.animationFile = animationFile;
	}

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	public AnimatedModelRenderer getBone(String boneName)
	{
		return modelRendererList.stream().filter(x -> x.name.equals(boneName)).findFirst().orElse(
				null);
	}

	/**
	 * Register model renderer. Each AnimatedModelRenderer (group in blockbench) NEEDS to be registered via this method.
	 *
	 * @param modelRenderer The model renderer
	 */
	public void registerModelRenderer(AnimatedModelRenderer modelRenderer)
	{
		modelRenderer.saveInitialSnapshot();
		modelRendererList.add(modelRenderer);
	}

	/**
	 * Gets a json animation by name.
	 *
	 * @param name The name
	 * @return the animation by name
	 * @throws JsonException
	 */
	public Map.Entry<String, JsonElement> getAnimationByName(String name) throws JsonException
	{
		return JsonAnimationUtils.getAnimation(getAnimationFile(), name);
	}


	/**
	 * Sets a rotation angle.
	 *
	 * @param modelRenderer The animated model renderer
	 * @param x             x
	 * @param y             y
	 * @param z             z
	 */
	public void setRotationAngle(AnimatedModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entityIn, float limbSwing, float limbSwingAmount, float partialTick)
	{
		T entity = (T) entityIn;

		// Keeps track of which bones have had animations applied to them, and eventually sets the ones that don't have an animation to their default values
		EntityDirtyTracker modelTracker = createNewDirtyTracker();

		// Adding partial tick to smooth out the animation

		// Each animation has it's own collection of animations (called the EntityAnimationManager), which allows for multiple independent animations
		EntityAnimationManager manager = entity.getAnimationManager();

		manager.tick = entity.ticksExisted + partialTick;
		double gameTick = manager.tick;
		double deltaTicks = gameTick - lastGameTickTime;
		seekTime += manager.getCurrentAnimationSpeed() * deltaTicks;
		lastGameTickTime = gameTick;

		// Store the current value of each bone rotation/position/scale
		if (manager.getBoneSnapshotCollection().isEmpty())
		{
			manager.setBoneSnapshotCollection(createNewBoneSnapshotCollection());
		}
		BoneSnapshotCollection boneSnapshots = manager.getBoneSnapshotCollection();

		for (AnimationController<T> controller : manager.values())
		{

			AnimationTestEvent<T> animationTestEvent = new AnimationTestEvent<T>(entity, seekTime, limbSwing,
					limbSwingAmount, partialTick, controller, !(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F));
			controller.isJustStarting = manager.isFirstTick;
			// Process animations and add new values to the point queues
			controller.process(seekTime, animationTestEvent, modelRendererList, boneSnapshots);

			// Loop through every single bone and lerp each property
			for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values())
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
					bone.rotateAngleX = AnimationUtils.lerpValues(rXPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueX;
					bone.rotateAngleY = AnimationUtils.lerpValues(rYPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueY;
					bone.rotateAngleZ = AnimationUtils.lerpValues(rZPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueZ;
					snapshot.rotationValueX = bone.rotateAngleX;
					snapshot.rotationValueY = bone.rotateAngleY;
					snapshot.rotationValueZ = bone.rotateAngleZ;
					snapshot.isCurrentlyRunningRotationAnimation = true;

					modelTracker.get(bone).hasRotationChanged = true;
				}
				// If there's any position points for this bone
				if (pXPoint != null && pYPoint != null && pZPoint != null)
				{
					bone.positionOffsetX = AnimationUtils.lerpValues(pXPoint, controller.easingType,
							controller.customEasingMethod);
					bone.positionOffsetY = AnimationUtils.lerpValues(pYPoint, controller.easingType,
							controller.customEasingMethod);
					bone.positionOffsetZ = AnimationUtils.lerpValues(pZPoint, controller.easingType,
							controller.customEasingMethod);
					snapshot.positionOffsetX = bone.positionOffsetX;
					snapshot.positionOffsetY = bone.positionOffsetY;
					snapshot.positionOffsetZ = bone.positionOffsetZ;
					snapshot.isCurrentlyRunningPositionAnimation = true;

					modelTracker.get(bone).hasPositionChanged = true;
				}

				// If there's any scale points for this bone
				if (sXPoint != null && sYPoint != null && sZPoint != null)
				{
					bone.scaleValueX = AnimationUtils.lerpValues(sXPoint, controller.easingType,
							controller.customEasingMethod);
					bone.scaleValueY = AnimationUtils.lerpValues(sYPoint, controller.easingType,
							controller.customEasingMethod);
					bone.scaleValueZ = AnimationUtils.lerpValues(sZPoint, controller.easingType,
							controller.customEasingMethod);
					snapshot.scaleValueX = bone.scaleValueX;
					snapshot.scaleValueY = bone.scaleValueY;
					snapshot.scaleValueZ = bone.scaleValueZ;
					snapshot.isCurrentlyRunningScaleAnimation = true;

					modelTracker.get(bone).hasScaleChanged = true;
				}
			}
		}

		double resetTickLength = manager.getResetSpeed();
		for (DirtyTracker tracker : modelTracker)
		{
			AnimatedModelRenderer model = tracker.model;
			BoneSnapshot initialSnapshot = model.getInitialSnapshot();
			BoneSnapshot saveSnapshot = boneSnapshots.get(tracker.model.name);

			if (!tracker.hasRotationChanged)
			{
				if (saveSnapshot.isCurrentlyRunningRotationAnimation)
				{
					saveSnapshot.mostRecentResetRotationTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningRotationAnimation = false;
				}

				double percentageReset = Math.min((seekTime - saveSnapshot.mostRecentResetRotationTick) / resetTickLength, 1);

				model.rotateAngleX = AnimationUtils.lerpValues(percentageReset, saveSnapshot.rotationValueX,
						initialSnapshot.rotationValueX);
				model.rotateAngleY = AnimationUtils.lerpValues(percentageReset, saveSnapshot.rotationValueY,
						initialSnapshot.rotationValueY);
				model.rotateAngleZ = AnimationUtils.lerpValues(percentageReset, saveSnapshot.rotationValueZ,
						initialSnapshot.rotationValueZ);
				if(percentageReset >= 1)
				{
					saveSnapshot.rotationValueX = model.rotateAngleX;
					saveSnapshot.rotationValueY = model.rotateAngleY;
					saveSnapshot.rotationValueZ = model.rotateAngleZ;
				}
			}
			if (!tracker.hasPositionChanged)
			{
				if (saveSnapshot.isCurrentlyRunningPositionAnimation)
				{
					saveSnapshot.mostRecentResetPositionTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningPositionAnimation = false;
				}

				double percentageReset = Math.min((seekTime - saveSnapshot.mostRecentResetPositionTick) / resetTickLength, 1);

				model.positionOffsetX = AnimationUtils.lerpValues(percentageReset, saveSnapshot.positionOffsetX,
						initialSnapshot.positionOffsetX);
				model.positionOffsetY = AnimationUtils.lerpValues(percentageReset, saveSnapshot.positionOffsetY,
						initialSnapshot.positionOffsetY);
				model.positionOffsetZ = AnimationUtils.lerpValues(percentageReset, saveSnapshot.positionOffsetZ,
						initialSnapshot.positionOffsetZ);

				if(percentageReset >= 1)
				{
					saveSnapshot.positionOffsetX = model.positionOffsetX;
					saveSnapshot.positionOffsetY = model.positionOffsetY;
					saveSnapshot.positionOffsetZ = model.positionOffsetZ;
				}
			}
			if (!tracker.hasScaleChanged)
			{
				if (saveSnapshot.isCurrentlyRunningScaleAnimation)
				{
					saveSnapshot.mostRecentResetScaleTick = (float) seekTime;
					saveSnapshot.isCurrentlyRunningScaleAnimation = false;
				}

				double percentageReset = Math.min((seekTime - saveSnapshot.mostRecentResetScaleTick) / resetTickLength, 1);

				model.scaleValueX = AnimationUtils.lerpValues(percentageReset, saveSnapshot.scaleValueX,
						initialSnapshot.scaleValueX);
				model.scaleValueY = AnimationUtils.lerpValues(percentageReset, saveSnapshot.scaleValueY,
						initialSnapshot.scaleValueY);
				model.scaleValueZ = AnimationUtils.lerpValues(percentageReset, saveSnapshot.scaleValueZ,
						initialSnapshot.scaleValueZ);

				if(percentageReset >= 1)
				{
					saveSnapshot.scaleValueX = model.scaleValueX;
					saveSnapshot.scaleValueY = model.scaleValueY;
					saveSnapshot.scaleValueZ = model.scaleValueZ;
				}
			}
		}
		manager.isFirstTick = false;
	}

	private EntityDirtyTracker createNewDirtyTracker()
	{
		EntityDirtyTracker tracker = new EntityDirtyTracker();
		for (AnimatedModelRenderer bone : modelRendererList)
		{
			tracker.add(new DirtyTracker(false, false, false, bone));
		}
		return tracker;
	}

	private BoneSnapshotCollection createNewBoneSnapshotCollection()
	{
		BoneSnapshotCollection collection = new BoneSnapshotCollection();
		for (AnimatedModelRenderer bone : modelRendererList)
		{
			collection.put(bone.name, new BoneSnapshot(bone.getInitialSnapshot()));
		}
		return collection;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
	}

	/**
	 * Gets animation.
	 *
	 * @param name The name
	 * @return the animation
	 */
	public Animation getAnimation(String name)
	{
		return animationList.get(name);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		for (AnimatedModelRenderer model : rootBones)
		{
			model.render(scale);
		}
	}
}
