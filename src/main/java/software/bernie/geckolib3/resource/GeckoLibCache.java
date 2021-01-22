package software.bernie.geckolib3.resource;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.ArrayUtils;

import com.eliotlash.molang.MolangParser;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.file.AnimationFileLoader;
import software.bernie.geckolib3.file.GeoModelLoader;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.molang.MolangRegistrar;

public class GeckoLibCache {
    private HashMap<Identifier, AnimationFile> animations = new HashMap<>();
    private HashMap<Identifier, GeoModel> geoModels = new HashMap<>();
    private static GeckoLibCache INSTANCE;
    public final MolangParser parser = new MolangParser();
    private final AnimationFileLoader animationLoader;
    private final GeoModelLoader modelLoader;

    public HashMap<Identifier, AnimationFile> getAnimations()
    {
        if(!GeckoLib.hasInitialized)
        {
            throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
        }
        return animations;
    }

    public HashMap<Identifier, GeoModel> getGeoModels()
    {
        if(!GeckoLib.hasInitialized)
        {
            throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
        }
        return geoModels;
    }

    protected GeckoLibCache() {
        this.animationLoader = new AnimationFileLoader();
        this.modelLoader = new GeoModelLoader();
        MolangRegistrar.registerVars(parser);
    }


    public static GeckoLibCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeckoLibCache();
            return INSTANCE;
        }
        return INSTANCE;
    }

    public CompletableFuture<Void> resourceReload(ResourceReloadListener.Synchronizer stage, ResourceManager resourceManager, Profiler preparationsProfiler, Profiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    	ConcurrentHashMap<Identifier, AnimationFile> tempAnimations = new ConcurrentHashMap<>();
    	ConcurrentHashMap<Identifier, GeoModel> tempModels = new ConcurrentHashMap<>();

        CompletableFuture[] animationFileFutures = resourceManager.findResources("animations", fileName -> fileName.endsWith(".json"))
                .stream()
                .map(location -> CompletableFuture.supplyAsync(() -> location))
                .map(completable -> completable.thenAcceptAsync(resource -> tempAnimations.put(resource, animationLoader.loadAllAnimations(parser, resource, resourceManager))))
                .toArray(CompletableFuture[]::new);

        CompletableFuture[] geoModelFutures = resourceManager.findResources("geo", fileName -> fileName.endsWith(".json"))
                .stream()
                .map(location -> CompletableFuture.supplyAsync(() -> location))
                .map(completable -> completable.thenAcceptAsync(resource -> tempModels.put(resource, modelLoader.loadModel(resourceManager, resource))))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(ArrayUtils.addAll(animationFileFutures, geoModelFutures)).thenAccept(x -> {
        	//Retain our behavior of completely replacing the old model map on reload
			HashMap<Identifier, AnimationFile> hashAnim = new HashMap<>();
			hashAnim.putAll(tempAnimations);
			animations = hashAnim;

			HashMap<Identifier, GeoModel> hashModel = new HashMap<>();
			hashModel.putAll(tempModels);
			geoModels = hashModel;
        }).thenCompose(stage::whenPrepared);
    }
}