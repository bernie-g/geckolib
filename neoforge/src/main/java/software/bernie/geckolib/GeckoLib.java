package software.bernie.geckolib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import software.bernie.geckolib.network.GeckoLibNetworkingNeoForge;

@Mod(GeckoLibConstants.MODID)
public final class GeckoLib {
    public GeckoLib(IEventBus modBus) {
        GeckoLibNetworkingNeoForge.init(modBus);

        if (FMLEnvironment.dist == Dist.CLIENT)
            GeckoLibClient.init();
    }
}
