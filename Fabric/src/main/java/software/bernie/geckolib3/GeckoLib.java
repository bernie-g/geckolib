/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
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
			ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
					.registerReloadListener(new IdentifiableResourceReloadListener() {
						@Override
						public Identifier getFabricId() {
							return new Identifier(GeckoLib.ModID, "models");
						}

						@Override
						public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager,
								Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor,
								Executor applyExecutor) {
							return GeckoLibCache.getInstance().reload(synchronizer, manager, prepareProfiler,
									applyProfiler, prepareExecutor, applyExecutor);
						}
					});
		}
		hasInitialized = true;
	}
}
