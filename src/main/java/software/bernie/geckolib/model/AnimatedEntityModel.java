package software.bernie.geckolib.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.util.JSONException;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.IAnimatedEntity;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationControllerCollection;
import software.bernie.geckolib.animation.AnimationUtils;
import software.bernie.geckolib.animation.keyframe.*;
import software.bernie.geckolib.file.AnimationFileManager;
import software.bernie.geckolib.json.JSONAnimationUtils;

import java.util.*;

public abstract class AnimatedEntityModel<T extends Entity & IAnimatedEntity> extends EntityModel<T>
{
	private JsonObject animationFile;
	private AnimationFileManager animationFileManager;
	private List<AnimatedModelRenderer> modelRendererList = new ArrayList();
	private HashMap<String, Animation> animationList = new HashMap();
	public abstract ResourceLocation getAnimationFileLocation();

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
				animation = JSONAnimationUtils.deserializeJsonToAnimation(JSONAnimationUtils.getAnimation(getAnimationFile(), animationName));
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
		return modelRendererList.stream().filter(x -> x.getModelRendererName().equals(boneName)).findFirst().orElse(
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
	public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick)
	{
		AnimationControllerCollection animationControllers = entityIn.getAnimationControllers();
		for (AnimationController animationController : animationControllers.values())
		{
			float tick = entityIn.ticksExisted + partialTick;

			if (!animationController.getAnimationPredicate().test(entityIn, limbSwing, limbSwingAmount, partialTick,
					animationController.transitionState, animationController))
			{
				continue;
			}
			VectorKeyFrameList<KeyFrame<Float>> rotationKeyFrames;
			VectorKeyFrameList<KeyFrame<Float>> scaleKeyFrames;
			VectorKeyFrameList<KeyFrame<Float>> positionKeyFrames;

			Animation animation = animationController.getAnimation();
			if(animation == null)
			{
				continue;
			}
			Animation transitionAnimation = null;
			float transitionLength = 0;
			float animationLength = animation.animationLength;

			if (animationController.transitionState == TransitionState.JustStarted)
			{
				animationController.tickOffset = tick;
				animationController.transitionState = TransitionState.Transitioning;
				transitionLength = animationController.transitionLength;

				transitionAnimation = animationController.getTransitioningAnimation();
			}
			else if (animationController.transitionState == TransitionState.Transitioning && tick - animationController.tickOffset >= AnimationUtils.convertSecondsToTicks(
					animationController.transitionLength))
			{
				animationController.transitionState = TransitionState.NotTransitioning;
				animationController.manuallyReplaceAnimation();
				animation = animationController.getTransitioningAnimation();
				for (BoneAnimation boneAnimation : animation.boneAnimations)
				{
					getBone(boneAnimation.boneName).transitionState = TransitionState.NotTransitioning;
				}
				transitionAnimation = animationController.getTransitioningAnimation();
				animationLength = animationController.getAnimation().animationLength;
				animationController.tickOffset = tick + 0.001F;

			}

			for (BoneAnimation boneAnimation : animation.boneAnimations)
			{
				AnimatedModelRenderer bone = getBone(boneAnimation.boneName);
				transitionAnimation = animationController.getTransitioningAnimation();

				BoneSnapshot recentSnapshot = null;
				BoneSnapshot initialSnapshot = null;

				if (bone.transitionState == TransitionState.JustStarted)
				{
					bone.saveSnapshot();
					recentSnapshot = bone.getRecentSnapshot();
					initialSnapshot = bone.getInitialSnapshot();
					bone.transitionState = TransitionState.Transitioning;
				}

				if (bone.transitionState == TransitionState.Transitioning || animationController.transitionState == TransitionState.Transitioning)
				{
					recentSnapshot = bone.getRecentSnapshot();
					initialSnapshot = bone.getInitialSnapshot();
				}
				boolean loop = true;

				if (initialSnapshot != null)
				{

					loop = false;
					BoneAnimation newBoneAnimation = transitionAnimation.boneAnimations.stream().filter(
							x -> x.boneName.equals(boneAnimation.boneName)).findFirst().orElse(null);
					VectorKeyFrameList<KeyFrame<Float>> tempRotationKeyFrames = new VectorKeyFrameList();
					VectorKeyFrameList<KeyFrame<Float>> tempPositionKeyFrames = new VectorKeyFrameList();
					VectorKeyFrameList<KeyFrame<Float>> tempScaleKeyFrames = new VectorKeyFrameList();
					transitionLength = animationController.transitionLength;

					if (newBoneAnimation.rotationKeyFrames.xKeyFrames.size() >= 1)
					{
						Float rX = newBoneAnimation.rotationKeyFrames.xKeyFrames.get(0).getStartValue();
						Float rY = newBoneAnimation.rotationKeyFrames.yKeyFrames.get(0).getStartValue();
						Float rZ = newBoneAnimation.rotationKeyFrames.zKeyFrames.get(0).getStartValue();
						tempRotationKeyFrames.xKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.rotationValueX, rX));
						tempRotationKeyFrames.yKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.rotationValueY, rY));
						tempRotationKeyFrames.zKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.rotationValueZ, rZ));

					}
					else {
						tempRotationKeyFrames.xKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.rotationValueX, initialSnapshot.rotationValueX));
						tempRotationKeyFrames.yKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.rotationValueY, initialSnapshot.rotationValueY));
						tempRotationKeyFrames.zKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.rotationValueZ, initialSnapshot.rotationValueZ));
					}

					if (newBoneAnimation.positionKeyFrames.xKeyFrames.size() >= 1)
					{
						Float pX = newBoneAnimation.positionKeyFrames.xKeyFrames.get(0).getStartValue();
						Float pY = newBoneAnimation.positionKeyFrames.yKeyFrames.get(0).getStartValue();
						Float pZ = newBoneAnimation.positionKeyFrames.zKeyFrames.get(0).getStartValue();
						tempPositionKeyFrames.xKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.positionOffsetX, pX));
						tempPositionKeyFrames.yKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.positionOffsetY, pY));
						tempPositionKeyFrames.zKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.positionOffsetZ, pZ));
					}
					else {
						tempPositionKeyFrames.xKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.positionOffsetX, initialSnapshot.positionOffsetX));
						tempPositionKeyFrames.yKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.positionOffsetY, initialSnapshot.positionOffsetY));
						tempPositionKeyFrames.zKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.positionOffsetZ, initialSnapshot.positionOffsetZ));
					}
					if (newBoneAnimation.scaleKeyFrames.xKeyFrames.size() >= 1)
					{
						Float sX = newBoneAnimation.scaleKeyFrames.xKeyFrames.get(0).getStartValue();
						Float sY = newBoneAnimation.scaleKeyFrames.yKeyFrames.get(0).getStartValue();
						Float sZ = newBoneAnimation.scaleKeyFrames.zKeyFrames.get(0).getStartValue();

						tempScaleKeyFrames.xKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.scaleValueX, sX));
						tempScaleKeyFrames.yKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.scaleValueY, sY));
						tempScaleKeyFrames.zKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.scaleValueZ, sZ));

					}
					else
					{
						tempScaleKeyFrames.xKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.scaleValueX, initialSnapshot.scaleValueX));
						tempScaleKeyFrames.yKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.scaleValueY, initialSnapshot.scaleValueY));
						tempScaleKeyFrames.zKeyFrames = Arrays.asList(
								new KeyFrame(transitionLength, recentSnapshot.scaleValueZ, initialSnapshot.scaleValueZ));
					}
					rotationKeyFrames = tempRotationKeyFrames;
					positionKeyFrames = tempPositionKeyFrames;
					scaleKeyFrames = tempScaleKeyFrames;
					animationLength = transitionLength;
				}
				else
				{
					rotationKeyFrames = boneAnimation.rotationKeyFrames;
					scaleKeyFrames = boneAnimation.scaleKeyFrames;
					positionKeyFrames = boneAnimation.positionKeyFrames;
				}

				float adjustedTick = tick - animationController.tickOffset;
				if(adjustedTick < 0)
				{
					adjustedTick = 0.01F;
				}
				if (rotationKeyFrames.xKeyFrames.size() >= 1)
				{
					bone.rotateAngleX = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.xKeyFrames, adjustedTick,
							loop, animationLength);
					bone.rotateAngleY = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.yKeyFrames, adjustedTick,
							loop, animationLength);
					bone.rotateAngleZ = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.zKeyFrames, adjustedTick,
							loop, animationLength);
				}
				if (scaleKeyFrames.xKeyFrames.size() >= 1)
				{
					bone.scaleValueX = AnimationUtils.LerpKeyFrames(scaleKeyFrames.xKeyFrames, adjustedTick, loop,
							animationLength);
					bone.scaleValueY = AnimationUtils.LerpKeyFrames(scaleKeyFrames.yKeyFrames, adjustedTick, loop,
							animationLength);
					bone.scaleValueZ = AnimationUtils.LerpKeyFrames(scaleKeyFrames.zKeyFrames, adjustedTick, loop,
							animationLength);
				}
				if (positionKeyFrames.xKeyFrames.size() >= 1)
				{
					bone.positionOffsetX = AnimationUtils.LerpKeyFrames(positionKeyFrames.xKeyFrames, adjustedTick,
							loop,
							animationLength);
					bone.positionOffsetY = AnimationUtils.LerpKeyFrames(positionKeyFrames.yKeyFrames, adjustedTick,
							loop,
							animationLength);
					bone.positionOffsetZ = AnimationUtils.LerpKeyFrames(positionKeyFrames.zKeyFrames, adjustedTick,
							loop,
							animationLength);
				}
			}
		}
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{

	}

	public Animation getAnimation(String name)
	{
		return animationList.get(name);
	}
}
