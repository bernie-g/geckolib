package software.bernie.geckolib;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class GeckoLibConstants {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "geckolib";
    public static final String DISABLE_EXAMPLES_PROPERTY_KEY = "geckolib.disable_examples";

    /**
     * Helper method to create a ResourceLocation predefined with GeckoLib's {@link #MODID}
     */
    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(GeckoLibConstants.MODID, path);
    }

    /**
     * By default, GeckoLib will register and activate several example entities, items, and blocks when in dev
     * <p>
     * These examples are <u>not</u> present when in a production environment (normal players)
     * <p>
     * This can be disabled by setting the {@link #DISABLE_EXAMPLES_PROPERTY_KEY} to false in your run args
     */
    public static boolean shouldRegisterExamples() {
        return GeckoLibServices.PLATFORM.isDevelopmentEnvironment() && !Boolean.getBoolean(GeckoLibConstants.DISABLE_EXAMPLES_PROPERTY_KEY);
    }
}
