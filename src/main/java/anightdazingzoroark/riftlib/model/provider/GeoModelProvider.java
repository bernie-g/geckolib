package anightdazingzoroark.riftlib.model.provider;

import net.minecraft.util.ResourceLocation;
import anightdazingzoroark.riftlib.geo.render.built.GeoModel;
import anightdazingzoroark.riftlib.resource.RiftLibCache;

public abstract class GeoModelProvider<T> {
	public double seekTime;
	public double lastGameTickTime;
	public boolean shouldCrashOnMissing = false;

	public GeoModel getModel(ResourceLocation location) {
		return RiftLibCache.getInstance().getGeoModels().get(location);
	}

	public abstract ResourceLocation getModelLocation(T object);

	public abstract ResourceLocation getTextureLocation(T object);
}
