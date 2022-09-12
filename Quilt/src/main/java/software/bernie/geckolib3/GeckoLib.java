/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class GeckoLib {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String ModID = "geckolib3";
	public static boolean hasInitialized;

	public static void initialize() {
		if (!hasInitialized) {
			ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(new IdentifiableResourceReloader() {
				@Override
				public Identifier getQuiltId() {
					return new Identifier(GeckoLib.ModID, "models");
				}

				@Override
				public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager,
						Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor,
						Executor applyExecutor) {
					return GeckoLibCache.getInstance().reload(synchronizer, manager, prepareProfiler, applyProfiler,
							prepareExecutor, applyExecutor);
				}
			});
		}
		hasInitialized = true;
	}
}
