package software.bernie.geckolib3.resource;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import software.bernie.geckolib3.GeckoLib;

public class ResourceListener {
	public static void registerReloadListener() {
		if (MinecraftClient.getInstance() != null) {
			if (MinecraftClient.getInstance().getResourceManager() == null) {
				throw new RuntimeException(
						"GeckoLib was initialized too early! If you are on fabric, please read the wiki on when to initialize!");
			}
			ReloadableResourceManagerImpl reloadable = (ReloadableResourceManagerImpl) MinecraftClient.getInstance()
					.getResourceManager();
			reloadable.registerReloader(GeckoLibCache.getInstance()::reload);
		} else {
			GeckoLib.LOGGER.warn(
					"Minecraft.getInstance() was null, could not register reload listeners. Ignore if datagenning.");
		}
	}
}
