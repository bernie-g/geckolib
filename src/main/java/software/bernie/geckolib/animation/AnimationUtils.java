/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;

public class AnimationUtils
{
	public static double convertTicksToSeconds(double ticks)
	{
		return ticks / 20;
	}

	public static double convertSecondsToTicks(double seconds)
	{
		return seconds * 20;
	}

	/**
	 * This is the actual function that smoothly interpolates (lerp) between keyframes
	 *
	 * @param currentTick The current tick (usually entity.ticksExisted + partialTicks to make it smoother)
	 * @param position    The animation's length in seconds
	 * @param startValue  The animation's start value
	 * @param endValue    The animation's end value
	 * @return The interpolated value
	 */
	public static float lerpValues(double currentTick, double position, double startValue, double endValue)
	{
		if (currentTick > position)
		{
			return (float) endValue;
		}
		if(currentTick == 0 && position == 0)
		{
			return (float) endValue;
		}
		// current tick / position should be between 0 and 1 and represent the percentage of the lerping that has completed
		return (float) (MathHelper.lerp(currentTick / position, startValue,
				endValue) * position / position);
	}

	/**
	 * Lerps an AnimationPoint
	 *
	 * @param animationPoint The animation point
	 * @return the resulting lerped value
	 */
	public static float lerpValues(AnimationPoint animationPoint)
	{
		return lerpValues(animationPoint.currentTick, animationPoint.animationEndTick, animationPoint.animationStartValue, animationPoint.animationEndValue);
	}

	/**
	 * Gets the renderer for an entity
	 */
	public static <T extends Entity> EntityRenderer<T> getRenderer(T entity)
	{
		EntityRenderDispatcher renderManager = MinecraftClient.getInstance().getEntityRenderManager();
		return (EntityRenderer<T>) renderManager.getRenderer(entity);
	}

	/**
	 * Gets the AnimatedEntityModel for an entity.
	 */
	public static <T extends Entity> AnimatedEntityModel getModelForEntity(T entity)
	{
		EntityRenderer<T> entityRenderer = getRenderer(entity);
		if (entityRenderer instanceof FeatureRendererContext)
		{
			LivingEntityRenderer renderer = (LivingEntityRenderer) entityRenderer;
			EntityModel entityModel = renderer.getModel();
			if (entityModel instanceof AnimatedEntityModel)
			{
				return (AnimatedEntityModel) entityModel;
			}
			else {
				GeckoLib.LOGGER.error("Model for " + entity.getName() + " is not an AnimatedEntityModel. Please inherit the proper class.");
				return null;
			}
		}
		else {
			GeckoLib.LOGGER.error("Could not find valid renderer for " + entity.getName());
			return null;
		}
	}

}
