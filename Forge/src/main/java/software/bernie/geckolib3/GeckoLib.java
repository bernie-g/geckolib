package software.bernie.geckolib3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.cache.GeckoLibCache;

/**
 * Base class for Geckolib!<br>
 * Hello World!<br>
 * There's not much to really see here, but feel free to stay a while and have a snack or something.
 * @see software.bernie.geckolib3.util.GeckoLibUtil
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Getting-Started">GeckoLib Wiki - Getting Started</a>
 */
public class GeckoLib {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "geckolib3";
	public static volatile boolean hasInitialized;

	/**
	 * This method <u><b>MUST</b></u> be called in your mod's constructor or during {@code onInitializeClient} in Fabric/Quilt.<br>
	 * If shadowing {@code GeckoLib}, you should instead call {@link GeckoLib#shadowInit}
	 */
	synchronized public static void initialize() {
		if (!hasInitialized) {
			DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> GeckoLibCache::registerReloadListener);
			GeckoLibNetwork.initialize();
		}

		hasInitialized = true;
	}

	/**
	 * Call this method instead of {@link GeckoLib#initialize} if you are shadowing the mod.
	 */
	synchronized public static void shadowInit() {
		if (!hasInitialized)
			DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> GeckoLibCache::registerReloadListener);

		hasInitialized = true;
	}
}
