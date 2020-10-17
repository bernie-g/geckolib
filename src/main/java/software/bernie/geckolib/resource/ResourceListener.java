package software.bernie.geckolib.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;

public class ResourceListener
{
	public static void registerReloadListener()
	{
		if (Minecraft.getInstance().getResourceManager() == null)
		{
			throw new RuntimeException("GeckoLib was initialized too early! If you are on fabric, please read the wiki on when to initialize!");
		}
		IReloadableResourceManager reloadable = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		reloadable.addReloadListener(GeckoLibCache.getInstance()::resourceReload);
	}
}
