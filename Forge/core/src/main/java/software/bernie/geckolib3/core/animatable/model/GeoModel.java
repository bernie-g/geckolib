package software.bernie.geckolib3.core.animatable.model;

import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.Animation;
import software.bernie.geckolib3.core.animation.AnimationProcessor;
import software.bernie.geckolib3.core.animation.AnimationEvent;

import java.util.Optional;

/**
 * Base class for Geckolib models.<br>
 * Mostly an internal placeholder to allow for splitting up core (non-Minecraft) libraries
 */
public interface GeoModel<E extends GeoAnimatable> {
	/**
	 * Get the baked model data for this model based on the provided string location
	 * @param location The resource path of the baked model (usually the animatable's id string)
	 * @return The BakedGeoModel
	 */
	BakedGeoModel getBakedGeoModel(String location);

	/**
	 * Gets a bone from this model by name.<br>
	 * Generally not a very efficient method, should be avoided where possible.
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link GeoBone} if one matches, otherwise an empty Optional
	 */
	default Optional<? extends GeoBone> getBone(String name) {
		return Optional.ofNullable(getAnimationProcessor().getBone(name));
	}

	/**
	 * Gets the {@link AnimationProcessor} for this model.
	 */
	AnimationProcessor<E> getAnimationProcessor();

	/**
	 * Gets the loaded {@link Animation} for the given animation {@code name}, if it exists
	 * @param animatable The {@code GeoAnimatable} instance being referred to
	 * @param name The name of the animation to retrieve
	 * @return The {@code Animation} instance for the provided {@code name}, or null if none match
	 */
	Animation getAnimation(E animatable, String name);

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered.<br>
	 * It is an internal method for automated animation parsing. Use {@link GeoModel#setCustomAnimations(GeoAnimatable, int, AnimationEvent)} for custom animation work
	 */
	void handleAnimations(E animatable, int instanceId, AnimationEvent<E> animationEvent);

	/**
	 * This method is called once per render frame for each {@link software.bernie.geckolib3.core.animatable.GeoAnimatable} being rendered.<br>
	 * Override to set custom animations (such as head rotation, etc).
	 * @param animatable The {@code GeoAnimatable} instance currently being rendered
	 * @param instanceId The instance id of the {@code GeoAnimatable}
	 * @param animationEvent An {@link AnimationEvent} instance created to hold animation data for the {@code animatable} for this method call
	 */
	default void setCustomAnimations(E animatable, int instanceId, AnimationEvent<E> animationEvent) {}

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered.<br>
	 * Is generally overridden by default to apply the builtin queries, but can be extended further for custom query handling.
	 * @param animatable The {@code GeoAnimatable} instance currently being rendered
	 * @param seekTime The time (in partial ticks) since last rendered tick
	 */
	default void applyMolangQueries(E animatable, double seekTime) {}
}
