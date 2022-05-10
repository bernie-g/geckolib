/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class AnimationUtils {
	public static double convertTicksToSeconds(double ticks) {
		return ticks / 20;
	}

	public static double convertSecondsToTicks(double seconds) {
		return seconds * 20;
	}

	/**
	 * Gets the renderer for an entity
	 */
	public static <T extends Entity> Render<T> getRenderer(T entity) {
		RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

		return renderManager.getEntityRenderObject(entity);
	}

	@SuppressWarnings("rawtypes")
	public static <T extends Entity> GeoModelProvider getGeoModelForEntity(T entity) {
		Render<T> entityRenderer = getRenderer(entity);

		if (entityRenderer instanceof IGeoRenderer) {
			return ((IGeoRenderer<?>) entityRenderer).getGeoModelProvider();
		}

		return null;
	}
}
