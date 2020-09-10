package software.bernie.geckolib.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.geo.render.built.GeoModel;

public interface IGeoModelProvider<T>
{
	GeoModel getModel(ResourceLocation location);

	ResourceLocation getModelLocation(T animatable);
}
