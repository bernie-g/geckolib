package software.bernie.geckolib;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.network.GeckoLibNetworkingForge;

@Mod(GeckoLibConstants.MODID)
public final class GeckoLib {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS_REGISTER = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE.key(), GeckoLibConstants.MODID);

    public GeckoLib() {
        GeckoLibNetworkingForge.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> GeckoLibCache::registerReloadListener);
    }
}
