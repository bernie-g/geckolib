package software.bernie.geckolib.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.util.JSONException;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Rotations;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationCategory;
import software.bernie.geckolib.animation.AnimationUtils;
import software.bernie.geckolib.animation.keyframe.*;
import software.bernie.geckolib.file.AnimationFileManager;
import software.bernie.geckolib.json.JSONAnimationUtils;

import java.util.*;

public abstract class AnimatedEntityModel<T extends Entity> extends EntityModel<T>
{
	private JsonObject animationFile;
	private AnimationFileManager animationFileManager;
	private List<AnimationCategory> activeAnimationCategories = new ArrayList();
	private List<AnimatedModelRenderer> modelRendererList = new ArrayList();

	public abstract ResourceLocation getAnimationFileLocation();

	public AnimatedEntityModel()
	{
		super();
		try
		{
			animationFileManager = new AnimationFileManager(getAnimationFileLocation());
			setAnimationFile(animationFileManager.loadAnimationFile());
		}
		catch (Exception e)
		{
			GeckoLib.LOGGER.error("Encountered error while loading animation file", e);
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

	public void resetAllAnimations()
	{
		activeAnimationCategories.clear();
	}

	public List<AnimationCategory> getActiveAnimationCategories()
	{
		return activeAnimationCategories;
	}

	public AnimatedModelRenderer getBone(String boneName)
	{
		return modelRendererList.stream().filter(x -> x.getModelRendererName().equals(boneName)).findFirst().orElse(null);
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
		float tick = entityIn.ticksExisted + partialTick;
		for (AnimationCategory animationCategory : activeAnimationCategories)
		{
			VectorKeyFrameList<RotationKeyFrame> rotationKeyFrames = new VectorKeyFrameList<>();
			VectorKeyFrameList<ScaleKeyFrame> scaleKeyFrames = new VectorKeyFrameList<>();
			VectorKeyFrameList<PositionKeyFrame> positionKeyFrames = new VectorKeyFrameList<>();


			Animation animation = animationCategory.getAnimation();
			float animationLength = animation.animationLength;

			for (BoneAnimation boneAnimation : animation.boneAnimations)
			{
				AnimatedModelRenderer bone = getBone(boneAnimation.boneName);


				if (animationCategory.transitionState == TransitionState.Transitioning)
				{
					Animation transitioningAnimation = animationCategory.getTransitioningAnimation();
					float transitionSpeed = animationCategory.transitionSpeed;
					BoneSnapshot boneSnapshot;

					boolean isBonePartOfNewAnimation = AnimationUtils.isBonePartOfAnimation(bone,
							transitioningAnimation);
					if(bone.transitionState == TransitionState.NotTransitioning)
					{
						bone.transitionState = TransitionState.Transitioning;
						animationCategory.transitionStartTick = tick;
						if (isBonePartOfNewAnimation)
						{
							// This means the bone is part of the new animation, and it will try to transition to the first rotation value of the animation
							bone.saveSnapshot();
						}
					}
					if (isBonePartOfNewAnimation){
						// This means the bone is part of the new animation, and it will try to transition to the first rotation value of the animation
						boneSnapshot = bone.getRecentSnapshot();
					}
					else {
						// Set the snapshot to the initial values of that bone (aka the condition when no animations are applied)
						boneSnapshot = bone.getInitialSnapshot();
					}
					rotationKeyFrames.xKeyFrames = Arrays.asList(new RotationKeyFrame(transitionSpeed, boneSnapshot.rotationValueX, 0));

				}
				else {
					rotationKeyFrames = boneAnimation.rotationKeyFrames;
					scaleKeyFrames = boneAnimation.scaleKeyFrames;
					positionKeyFrames = boneAnimation.positionKeyFrames;
				}
				if (rotationKeyFrames.xKeyFrames.size() > 1)
				{
					bone.rotateAngleX = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.xKeyFrames, tick,
							true, animationLength);
					bone.rotateAngleY = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.yKeyFrames, tick,
							true, animationLength);
					bone.rotateAngleZ = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.zKeyFrames, tick,
							true, animationLength);
				}
				if (scaleKeyFrames.xKeyFrames.size() > 1)
				{
					bone.scaleValueX = AnimationUtils.LerpKeyFrames(scaleKeyFrames.xKeyFrames, tick, true,
							animationLength);
					bone.scaleValueY = AnimationUtils.LerpKeyFrames(scaleKeyFrames.yKeyFrames, tick, true,
							animationLength);
					bone.scaleValueZ = AnimationUtils.LerpKeyFrames(scaleKeyFrames.zKeyFrames, tick, true,
							animationLength);
				}
				if (positionKeyFrames.xKeyFrames.size() > 1)
				{
					bone.positionOffsetX = AnimationUtils.LerpKeyFrames(positionKeyFrames.xKeyFrames, tick, true,
							animationLength);
					bone.positionOffsetY = AnimationUtils.LerpKeyFrames(positionKeyFrames.yKeyFrames, tick, true,
							animationLength);
					bone.positionOffsetZ = AnimationUtils.LerpKeyFrames(positionKeyFrames.zKeyFrames, tick, true,
							animationLength);
				}
			}
		}
	}

	/**
	 * Sets this entity's model rotation angles
	 *
	 * @param entityIn
	 * @param limbSwing
	 * @param limbSwingAmount
	 * @param ageInTicks
	 * @param netHeadYaw
	 * @param headPitch
	 */
	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{

	}
}
