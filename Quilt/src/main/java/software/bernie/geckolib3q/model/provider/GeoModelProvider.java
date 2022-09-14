package software.bernie.geckolib3q.model.provider;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.resource.GeckoLibCache;

public abstract class GeoModelProvider<T> {
	public double seekTime;
	public double lastGameTickTime;
	public boolean shouldCrashOnMissing = false;

	public GeoModel getModel(ResourceLocation location) {
		return GeckoLibCache.getInstance().getGeoModels().get(location);
	}

	public abstract ResourceLocation getModelResource(T object);

	public abstract ResourceLocation getTextureResource(T object);
}