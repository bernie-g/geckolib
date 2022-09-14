package software.bernie.geckolib3.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import software.bernie.geckolib3.GeckoLib;

public class ResourceListener {
	public static void registerReloadListener() {
		if (Minecraft.getInstance() != null) {
			if (Minecraft.getInstance().getResourceManager() == null) {
				throw new RuntimeException(
						"GeckoLib was initialized too early! If you are on fabric, please read the wiki on when to initialize!");
			}
			ReloadableResourceManager reloadable = (ReloadableResourceManager) Minecraft.getInstance()
					.getResourceManager();
			reloadable.registerReloadListener(GeckoLibCache.getInstance()::reload);
		} else {
			GeckoLib.LOGGER.warn(
					"Minecraft.getInstance() was null, could not register reload listeners. Ignore if datagenning.");
		}
	}
}
