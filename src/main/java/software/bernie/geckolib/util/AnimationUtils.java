/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.core.keyframe.AnimationPoint;
import software.bernie.geckolib.core.easing.EasingManager;
import software.bernie.geckolib.core.easing.EasingType;
import software.bernie.geckolib.renderers.geo.IGeoRenderer;
import software.bernie.geckolib.model.AnimatedEntityModel;
import software.bernie.geckolib.model.provider.GeoModelProvider;

import java.util.function.Function;

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
	 * Gets the renderer for an entity
	 */
	public static <T extends Entity> EntityRenderer<T> getRenderer(T entity)
	{
		EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
		return (EntityRenderer<T>) renderManager.getRenderer(entity);
	}

	/**
	 * Gets the AnimatedEntityModel for an entity.
	 */
	public static <T extends Entity> AnimatedEntityModel getModelForEntity(T entity)
	{
		EntityRenderer<T> entityRenderer = getRenderer(entity);
		if (entityRenderer instanceof IEntityRenderer)
		{
			if (entityRenderer instanceof LivingRenderer)
			{

				LivingRenderer renderer = (LivingRenderer) entityRenderer;
				EntityModel entityModel = renderer.getEntityModel();
				if (entityModel instanceof AnimatedEntityModel)
				{
					return (AnimatedEntityModel) entityModel;
				}
				else
				{
					GeckoLib.LOGGER.error("Model for {} is not an AnimatedEntityModel. Please inherit the proper class.", entity.getName());
					return null;
				}
			}
		}
		return null;
	}

	public static <T extends Entity> GeoModelProvider getGeoModelForEntity(T entity)
	{
		EntityRenderer<T> entityRenderer = getRenderer(entity);

		if (entityRenderer instanceof IGeoRenderer)
		{
			return ((IGeoRenderer<?>) entityRenderer).getGeoModelProvider();
		}
		return null;
	}
}
