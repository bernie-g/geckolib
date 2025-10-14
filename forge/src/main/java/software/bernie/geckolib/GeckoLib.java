package software.bernie.geckolib;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import software.bernie.geckolib.network.GeckoLibNetworkingForge;

@Mod(GeckoLibConstants.MODID)
public final class GeckoLib {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS_REGISTER = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE.key(), GeckoLibConstants.MODID);

    public GeckoLib(FMLJavaModLoadingContext context) {
        DATA_COMPONENTS_REGISTER.register(context.getModBusGroup());
        GeckoLibNetworkingForge.init();
    }
}
