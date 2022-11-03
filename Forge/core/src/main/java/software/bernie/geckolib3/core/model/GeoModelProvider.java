package software.bernie.geckolib3.core.model;

/**
 * Model provider for Geckolib models.
 * Mostly a placeholder to allow for splitting up core (non-Minecraft) libraries
 */
@FunctionalInterface
public interface GeoModelProvider {
	GeoModel getModel(String id);
}
