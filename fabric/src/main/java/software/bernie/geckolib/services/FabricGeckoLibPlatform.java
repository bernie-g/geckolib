package software.bernie.geckolib.services;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricGeckoLibPlatform implements GeckoLibPlatform {

    @Override
    public boolean isDevelopmentEnvironment(){
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getGameDir(){
        return FabricLoader.getInstance().getGameDir();
    }
}
