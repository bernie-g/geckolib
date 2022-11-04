package software.bernie.geckolib3.core.animatable.model;

/**
 * Model provider for Geckolib models.<br>
 * Mostly an internal placeholder to allow for splitting up core (non-Minecraft) libraries
 */
@FunctionalInterface
public interface GeoModelProvider {
	GeoModel getModel(String id);
}
