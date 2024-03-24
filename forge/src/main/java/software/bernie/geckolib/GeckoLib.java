package software.bernie.geckolib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.network.GeckoLibNetworkingForge;

@Mod(GeckoLibConstants.MODID)
public final class GeckoLib {
    public GeckoLib() {
        GeckoLibNetworkingForge.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> GeckoLibCache::registerReloadListener);
    }
}
