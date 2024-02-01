package software.bernie.geckolib.services;

import java.nio.file.Path;

public interface GeckoLibPlatform {

    boolean isDevelopmentEnvironment();

    Path getGameDir();
}