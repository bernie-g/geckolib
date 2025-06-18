package anightdazingzoroark.riftlib.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;

public class ResourceListener {
	public static void registerReloadListener() {
		if (Minecraft.getMinecraft().getResourceManager() == null) {
			throw new RuntimeException(
					"GeckoLib was initialized too early! If you are on fabric, please read the wiki on when to initializeMiddle!");
		}
		IReloadableResourceManager reloadable = (IReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager();
		reloadable.registerReloadListener(RiftLibCache.getInstance());
	}
}
