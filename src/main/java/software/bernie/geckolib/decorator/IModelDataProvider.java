package software.bernie.geckolib.decorator;

import java.util.HashMap;

public interface IModelDataProvider
{
	HashMap<Class<ExtraModelData>, ExtraModelData> getAllModelData();

	default <T extends ExtraModelData> T getModelData(Class<T> type)
	{
		return (T) getAllModelData().get(type);
	}


	default <T extends ExtraModelData> void putModelData(Class<T> type, T value)
	{
		getAllModelData().put((Class<ExtraModelData>) type, value);
	}
}
