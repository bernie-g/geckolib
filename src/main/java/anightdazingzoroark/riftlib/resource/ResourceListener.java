package anightdazingzoroark.riftlib.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;

public class ResourceListener {
	public static void registerReloadListener() {
		if (Minecraft.getMinecraft().getResourceManager() == null) {
			throw new RuntimeException(
					"RiftLib was initialized too early!");
		}
		IReloadableResourceManager reloadable = (IReloadableResourceManager) Minecraft.getMinecraft()
				.getResourceManager();
		reloadable.registerReloadListener(RiftLibCache.getInstance());
	}
}
