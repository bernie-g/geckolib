/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3q;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import software.bernie.geckolib3q.resource.GeckoLibCache;

public class GeckoLib {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String ModID = "geckolib3q";
	public static boolean hasInitialized;

	public static void initialize() {
		if (!hasInitialized) {
			ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(new IdentifiableResourceReloader() {
				@Override
				public ResourceLocation getQuiltId() {
					return new ResourceLocation(GeckoLib.ModID, "models");
				}

				@Override
				public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager,
						ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor,
						Executor applyExecutor) {
					return GeckoLibCache.getInstance().reload(synchronizer, manager, prepareProfiler, applyProfiler,
							prepareExecutor, applyExecutor);
				}
			});
		}
		hasInitialized = true;
	}
}
