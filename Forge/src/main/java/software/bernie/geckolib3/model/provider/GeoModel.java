package software.bernie.geckolib3.model.provider;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animatable.model.GeoModelProvider;
import software.bernie.geckolib3.geo.render.built.BakedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

public abstract class GeoModel<T extends GeoAnimatable> implements GeoModelProvider, software.bernie.geckolib3.core.animatable.model.GeoModel<T> {
	public boolean shouldCrashOnMissing = false;

	public BakedGeoModel getBakedModel(ResourceLocation location) {
		return GeckoLibCache.getBakedModels().get(location);
	}

	public abstract ResourceLocation getModelResource(T object);

	public abstract ResourceLocation getTextureResource(T object);
}
