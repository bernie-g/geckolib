package software.bernie.geckolib.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.geo.render.built.GeoModel;

public interface IGeoModel
{
	GeoModel getModel();
	ResourceLocation getModelLocation();
}
