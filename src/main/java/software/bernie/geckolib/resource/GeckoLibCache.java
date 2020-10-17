package software.bernie.geckolib.resource;

import com.eliotlash.molang.MolangParser;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.ArrayUtils;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.file.AnimationFile;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.file.GeoModelLoader;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.molang.MolangRegistrar;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
        HashMap<Identifier, AnimationFile> tempAnimations = new HashMap<>();
        HashMap<Identifier, GeoModel> tempModels = new HashMap<>();

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
            animations = tempAnimations;
            geoModels = tempModels;
        }).thenCompose(stage::whenPrepared);
    }
}