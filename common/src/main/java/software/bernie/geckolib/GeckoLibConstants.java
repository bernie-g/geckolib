package software.bernie.geckolib;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeckoLibConstants {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "geckolib";

    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(GeckoLibConstants.MODID, path);
    }


    //TODO bring block signature generic PR
    //TODO give generic to getTick(Object)
}
