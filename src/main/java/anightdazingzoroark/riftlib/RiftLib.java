package anightdazingzoroark.riftlib;

import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import anightdazingzoroark.riftlib.resource.ResourceListener;

public class RiftLib {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String ModID = "riftlib";
	private static boolean hasInitialized;
	public static final String VERSION = "1.0.0";

	/**
	 * This method MUST be called in your mod's constructor or during
	 * an FMLInitializationEvent, otherwise models and animations won't be
	 * loaded.
	 */
	public static void initialize() {
		if (!hasInitialized) {
			FMLCommonHandler.callFuture(new FutureTask<>(() -> {
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
					doOnlyOnClient();
				}
			}, null));
		}
		hasInitialized = true;
	}

	@SideOnly(Side.CLIENT)
	private static void doOnlyOnClient() {
		ResourceListener.registerReloadListener();
	}

	public static boolean isInitialized() {
		return hasInitialized;
	}
}
