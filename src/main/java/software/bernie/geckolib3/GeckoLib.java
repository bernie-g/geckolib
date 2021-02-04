package software.bernie.geckolib3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.resource.ResourceListener;

public class GeckoLib
{
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String ModID = "geckolib3";
	public static volatile boolean hasInitialized;

	/**
	 * This method MUST be called in your mod's constructor or during onInitializeClient in fabric, otherwise models and animations won't be loaded.
	 */
	synchronized public static void initialize()
	{
		if (!hasInitialized)
		{
			DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ResourceListener::registerReloadListener);
		}
		hasInitialized = true;
	}
}
