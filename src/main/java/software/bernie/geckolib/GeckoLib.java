package software.bernie.geckolib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.resource.ResourceListener;

public class GeckoLib
{
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String ModID = "geckolib";
	public static boolean hasInitialized;

	/**
	 * This method MUST be called in your mod's constructor or during onInitializeClient in fabric, otherwise models and animations won't be loaded.
	 */
	public static void initialize()
	{
		if (!hasInitialized)
		{
			DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ResourceListener::registerReloadListener);
		}
		hasInitialized = true;
	}
}
