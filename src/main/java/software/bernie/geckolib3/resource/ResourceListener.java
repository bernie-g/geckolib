package software.bernie.geckolib3.resource;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;

public class ResourceListener {
    public static void registerReloadListener() {
        if (MinecraftClient.getInstance().getResourceManager() == null) {
            throw new RuntimeException("GeckoLib was initialized too early! If you are on fabric, please read the wiki on when to initialize!");
        }
        ReloadableResourceManagerImpl reloadable = (ReloadableResourceManagerImpl) MinecraftClient.getInstance().getResourceManager();
        reloadable.registerListener(GeckoLibCache.getInstance()::resourceReload);
    }
}