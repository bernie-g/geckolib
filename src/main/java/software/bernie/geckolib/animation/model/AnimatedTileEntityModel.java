/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.controller.AnimationController;
import software.bernie.geckolib.manager.EntityAnimationManager;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.animation.snapshot.BoneSnapshot;
import software.bernie.geckolib.animation.snapshot.BoneSnapshotCollection;
import software.bernie.geckolib.animation.snapshot.DirtyTracker;
import software.bernie.geckolib.animation.snapshot.EntityDirtyTracker;
import software.bernie.geckolib.entity.IAnimatedEntity;
import software.bernie.geckolib.event.AnimationTestEvent;
import software.bernie.geckolib.reload.ReloadManager;
import software.bernie.geckolib.util.AnimationUtils;
import software.bernie.geckolib.util.json.JsonAnimationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * An AnimatedEntityModel is the equivalent of an Entity Model, except it provides extra functionality for rendering animations from bedrock json animation files. The entity passed into the generic parameter needs to implement IAnimatedEntity.
 *
 * @param <T> the type parameter
 */
public abstract class AnimatedTileEntityModel<T extends BlockEntity & IAnimatedEntity> extends BaseAnimatedModel<T> implements SynchronousResourceReloadListener
{
	private JsonObject animationFile;
	private List<AnimatedModelRenderer> modelRendererList = new ArrayList();
	private HashMap<String, Animation> animationList = new HashMap();
	public List<AnimatedModelRenderer> rootBones = new ArrayList<>();

	/**
	 * This resource location needs to point to a json file of your animation file, i.e. "geckolib:animations/frog_animation.json"
	 *
	 * @return the animation file location
	 */
	public abstract Identifier getAnimationFileLocation();

	/**
	 * If animations should loop by default and ignore their pre-existing loop settings (that you can enable in blockbench by right clicking)
	 */
	public boolean loopByDefault = false;

	/**
	 * Instantiates a new Animated entity model and loads the current animation file.
	 */
	protected AnimatedTileEntityModel()
	{
		super();
		ReloadManager.registerModel(this);
		ReloadableResourceManager resourceManager = (ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager();
		//resourceManager.addReloadListener(this);
		apply(resourceManager);
	}


	/**
	 * Internal method for handling reloads of animation files. Do not override.
	 */
	@Override
	public void apply(ResourceManager resourceManager)
	{
		try
		{
			Gson GSON = new Gson();
			ResourceImpl resource = (ResourceImpl) resourceManager.getResource(getAnimationFileLocation());
			Reader reader = new BufferedReader(
					new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
			JsonObject jsonobject = JsonHelper.deserialize(GSON, reader, JsonObject.class);
			resource.close();
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
			catch (ShaderParseException e)
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
	 */
	public Map.Entry<String, JsonElement> getAnimationByName(String name) throws ShaderParseException
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
		modelRenderer.pitch = x;
		modelRenderer.yaw = y;
		modelRenderer.roll = z;
	}

	@Override
	public void setLivingAnimations(T entity, int ticksExisted, float limbSwing, float limbSwingAmount, float partialTick)
	{
		// Keeps track of which bones have had animations applied to them, and eventually sets the ones that don't have an animation to their default values
		EntityDirtyTracker modelTracker = createNewDirtyTracker();

		// Adding partial tick to smooth out the animation
		double tick = ticksExisted + partialTick;

		// Each animation has it's own collection of animations (called the EntityAnimationManager), which allows for multiple independent animations
		EntityAnimationManager controllers = entity.getAnimationManager();

		// Store the current value of each bone rotation/position/scale
		if (controllers.getBoneSnapshotCollection().isEmpty())
		{
			controllers.setBoneSnapshotCollection(createNewBoneSnapshotCollection());
		}
		BoneSnapshotCollection boneSnapshots = controllers.getBoneSnapshotCollection();

		for (AnimationController<T> controller : controllers.values())
		{

			AnimationTestEvent<T> animationTestEvent = new AnimationTestEvent<T>(entity, tick, limbSwing,
					limbSwingAmount, partialTick, controller, !(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F));

			// Process animations and add new values to the point queues
			controller.process(tick, animationTestEvent, modelRendererList, boneSnapshots);

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
					bone.pitch = AnimationUtils.lerpValues(rXPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueX;
					bone.yaw = AnimationUtils.lerpValues(rYPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueY;
					bone.roll = AnimationUtils.lerpValues(rZPoint, controller.easingType,
							controller.customEasingMethod) + initialSnapshot.rotationValueZ;
					snapshot.rotationValueX = bone.pitch;
					snapshot.rotationValueY = bone.yaw;
					snapshot.rotationValueZ = bone.roll;

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
					modelTracker.get(bone).hasScaleChanged = true;
				}
			}
		}

		for (DirtyTracker tracker : modelTracker)
		{
			AnimatedModelRenderer model = tracker.model;
			BoneSnapshot initialSnapshot = model.getInitialSnapshot();
			BoneSnapshot saveSnapshot = boneSnapshots.get(tracker.model.name);

			if (!tracker.hasRotationChanged)
			{
				model.pitch = lerpConstant(saveSnapshot.rotationValueX, initialSnapshot.rotationValueX, 0.02);
				model.yaw = lerpConstant(saveSnapshot.rotationValueY, initialSnapshot.rotationValueY, 0.02);
				model.roll = lerpConstant(saveSnapshot.rotationValueZ, initialSnapshot.rotationValueZ, 0.02);
				saveSnapshot.rotationValueX = model.pitch;
				saveSnapshot.rotationValueY = model.yaw;
				saveSnapshot.rotationValueZ = model.roll;
			}
			if (!tracker.hasPositionChanged)
			{
				model.positionOffsetX = lerpConstant(saveSnapshot.positionOffsetX, initialSnapshot.positionOffsetX,
						0.02);
				model.positionOffsetY = lerpConstant(saveSnapshot.positionOffsetY, initialSnapshot.positionOffsetY,
						0.02);
				model.positionOffsetZ = lerpConstant(saveSnapshot.positionOffsetZ, initialSnapshot.positionOffsetZ,
						0.02);
				saveSnapshot.positionOffsetX = model.positionOffsetX;
				saveSnapshot.positionOffsetY = model.positionOffsetY;
				saveSnapshot.positionOffsetZ = model.positionOffsetZ;
			}
			if (!tracker.hasScaleChanged)
			{
				model.scaleValueX = lerpConstant(saveSnapshot.scaleValueX, initialSnapshot.scaleValueX, 0.02);
				model.scaleValueY = lerpConstant(saveSnapshot.scaleValueY, initialSnapshot.scaleValueY, 0.02);
				model.scaleValueZ = lerpConstant(saveSnapshot.scaleValueZ, initialSnapshot.scaleValueZ, 0.02);
				saveSnapshot.scaleValueX = model.scaleValueX;
				saveSnapshot.scaleValueY = model.scaleValueY;
				saveSnapshot.scaleValueZ = model.scaleValueZ;
			}
		}
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
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
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

	private static float lerpConstant(double currentValue, double finalValue, double speedModifier)
	{
		double lowerBound = finalValue - speedModifier;
		double upperBound = finalValue + speedModifier;

		if (lowerBound <= currentValue && upperBound >= currentValue)
		{
			return (float) currentValue;
		}
		double increment = 0;
		if (currentValue < finalValue)
		{
			increment = speedModifier;
		}
		else
		{
			increment = -1 * speedModifier;
		}

		return (float) (currentValue + increment);
	}

	@Override
	public void render(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		for (AnimatedModelRenderer model : rootBones)
		{
			model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
}
