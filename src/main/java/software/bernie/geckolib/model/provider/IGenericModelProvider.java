package software.bernie.geckolib.model.provider;

import software.bernie.geckolib.geo.render.built.GeoModel;

public interface IGenericModelProvider<T>
{
	void reload();
	default void reloadModel(GeoModel model){}
}
