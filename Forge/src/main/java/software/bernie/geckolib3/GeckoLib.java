package software.bernie.geckolib3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import software.bernie.geckolib3.core.animation.Animation;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.resource.ResourceListener;

public class GeckoLib {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String ModID = "geckolib3";
	public static volatile boolean hasInitialized;
	/**
	 * Call this because you {@link #initialize()} if you are shadowing GeckoLib
	 * into your mod and are not planning using items. This is the best fix I have
	 * for this currently.
	 */
	public static boolean DISABLE_NETWORKING = false;

	/**
	 * This method MUST be called in your mod's constructor or during
	 * onInitializeClient in fabric, otherwise models and animations won't be
	 * loaded. If you are shadowing Geckolib into your mod, don't call this, you
	 * will instead call
	 * 
	 * <pre>
	* {@code
	 * GeckoLib.hasInitialized = true;
	 * DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ResourceListener::registerReloadListener);
	* }
	 * </pre>
	 */
	synchronized public static void initialize() {
		if (!hasInitialized) {
			DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ResourceListener::registerReloadListener);
			GeckoLibNetwork.initialize();
		}
		hasInitialized = true;
	}

	/**
	 * Register a custom {@link software.bernie.geckolib3.core.animation.Animation.LoopType} with Geckolib,
	 * allowing for dynamic handling of post-animation looping
	 * @param name The name of the loop type handler
	 * @param loopType The LoopType implementation to use for the given name
	 */
	synchronized public static void addCustomLoopType(String name, Animation.LoopType loopType) {
		Animation.LoopType.addCustom(name, loopType);
	}
}
