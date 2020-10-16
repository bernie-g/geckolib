package software.bernie.geckolib.resource;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;

public class ResourceListener {
	public static void registerReloadListener() {
		ReloadableResourceManagerImpl reloadable = (ReloadableResourceManagerImpl) MinecraftClient.getInstance().getResourceManager();
		reloadable.registerListener(GeckoLibCache.getInstance()::resourceReload);
	}
}