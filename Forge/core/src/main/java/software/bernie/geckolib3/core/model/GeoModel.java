package software.bernie.geckolib3.core.model;

import java.util.Optional;

/**
 * Base class for Geckolib models
 * Mostly a placeholder to allow for splitting up core (non-Minecraft) libraries
 */
@FunctionalInterface
public interface GeoModel {
	Optional<GeoBone> getBone(String name);
}
