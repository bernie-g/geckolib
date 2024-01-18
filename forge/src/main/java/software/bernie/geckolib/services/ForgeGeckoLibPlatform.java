package software.bernie.geckolib.services;

import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgeGeckoLibPlatform implements GeckoLibPlatform {

    @Override
    public boolean isDevelopmentEnvironment(){
        return !FMLEnvironment.production;
    }

    @Override
    public Path getGameDir(){
        return FMLPaths.GAMEDIR.get();
    }
}
