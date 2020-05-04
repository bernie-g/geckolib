package software.bernie.geckolib;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.registry.Entities;

@Mod("geckolib")
public class GeckoLib
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String ModID = "geckolib";

    public GeckoLib() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        Entities.ENTITIES.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
