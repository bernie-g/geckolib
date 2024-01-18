package software.bernie.geckolib.services;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgeGeckoLibPlatform implements GeckoLibPlatform {

    @Override
    public boolean isDevelopmentEnvironment(){
        return !FMLEnvironment.production;
    }

    @Override
    public Path getGameDir(){
        return FMLPaths.GAMEDIR.get();
    }
}
