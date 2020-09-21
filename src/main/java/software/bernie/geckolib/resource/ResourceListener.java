package software.bernie.geckolib.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;

public class ResourceListener
{
	public static void registerReloadListener()
	{
		IReloadableResourceManager reloadable = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		reloadable.addReloadListener(GeckoLibCache.getInstance()::resourceReload);
	}
}
