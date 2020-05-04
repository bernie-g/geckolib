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
import software.bernie.geckolib.animation.AnimationUtils;
import software.bernie.geckolib.animation.keyframe.*;
import software.bernie.geckolib.file.AnimationFileManager;
import software.bernie.geckolib.json.JSONAnimationUtils;

import java.util.*;


public abstract class AnimatedEntityModel<T extends Entity> extends EntityModel<T>
{
	private JsonObject animationFile;
	private AnimationFileManager animationFileManager;
	private List<Animation> currentlyPlayingAnimations = new ArrayList();
	private List<AnimatedModelRenderer> modelRendererList = new ArrayList();


	public abstract ResourceLocation getAnimationFileLocation();

	public abstract String getDefaultAnimation();


	public AnimatedEntityModel()
	{
		super();
		try
		{
			animationFileManager = new AnimationFileManager(getAnimationFileLocation());
			setAnimationFile(animationFileManager.loadAnimationFile());
			updateCurrentAnimations(Arrays.asList(getAnimationByName(getDefaultAnimation())));
		}
		catch (Exception e)
		{
			GeckoLib.LOGGER.error(e);
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

	private void updateCurrentAnimations(List<Map.Entry<String, JsonElement>> animations)
	{
		for (Map.Entry<String, JsonElement> animation : animations)
		{
			currentlyPlayingAnimations.add(JSONAnimationUtils.deserializeJsonToAnimation(animation));
		}
	}

	public void resetAnimations()
	{
		currentlyPlayingAnimations.clear();
	}

	public List<Animation> getPlayingAnimations()
	{
		return currentlyPlayingAnimations;
	}

	public AnimatedModelRenderer getBone(String boneName)
	{
		return modelRendererList.stream().filter(x -> x.modelRendererName.equals(boneName)).findFirst().orElse(null);
	}

	public void registerModelRenderer(AnimatedModelRenderer modelRenderer)
	{
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
		modelRenderer.setInitialRotation(new Rotations(x, y, z));
	}

	@Override
	public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick)
	{
		float tick = entityIn.ticksExisted + partialTick;
		for (Animation animation : currentlyPlayingAnimations)
		{
			for (BoneAnimation boneAnimation : animation.boneAnimations)
			{
				AnimatedModelRenderer bone = getBone(boneAnimation.boneName);
				VectorKeyFrameList<RotationKeyFrame> rotationKeyFrames = boneAnimation.rotationKeyFrames;
				VectorKeyFrameList<ScaleKeyFrame> scaleKeyFrames = boneAnimation.scaleKeyFrames;
				VectorKeyFrameList<PositionKeyFrame> positionKeyFrames = boneAnimation.positionKeyFrames;

				Rotations defaultRotation = bone.getInitialRotation();
				if (rotationKeyFrames.getXKeyFrames().size() > 1)
				{
					bone.rotateAngleX = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.getXKeyFrames(), tick,
							true, animation.animationLength);
					bone.rotateAngleY = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.getYKeyFrames(), tick,
							true, animation.animationLength);
					bone.rotateAngleZ = AnimationUtils.LerpRotationKeyFrames(rotationKeyFrames.getZKeyFrames(), tick,
							true, animation.animationLength);
				}
				if (scaleKeyFrames.getXKeyFrames().size() > 1)
				{
					bone.scaleValueX = AnimationUtils.LerpKeyFrames(scaleKeyFrames.getXKeyFrames(), tick, true,
							animation.animationLength);
					bone.scaleValueY = AnimationUtils.LerpKeyFrames(scaleKeyFrames.getYKeyFrames(), tick, true,
							animation.animationLength);
					bone.scaleValueZ = AnimationUtils.LerpKeyFrames(scaleKeyFrames.getZKeyFrames(), tick, true,
							animation.animationLength);
				}
				if (positionKeyFrames.getXKeyFrames().size() > 1)
				{
					/*bone.positionOffsetX = AnimationUtils.LerpKeyFrames(positionKeyFrames.getXKeyFrames(), tick, true,
							animation.animationLength);
					bone.positionOffsetY = AnimationUtils.LerpKeyFrames(positionKeyFrames.getYKeyFrames(), tick, true,
							animation.animationLength);
					bone.positionOffsetZ = AnimationUtils.LerpKeyFrames(positionKeyFrames.getZKeyFrames(), tick, true,
							animation.animationLength);*/
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
