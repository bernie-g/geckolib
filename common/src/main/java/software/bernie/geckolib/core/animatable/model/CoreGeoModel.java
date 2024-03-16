package software.bernie.geckolib.core.animatable.model;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.Optional;

/**
 * Base class for Geckolib models.<br>
 * Mostly an internal placeholder to allow for splitting up core (non-Minecraft) libraries
 */
public interface CoreGeoModel<E extends GeoAnimatable> {
	/**
	 * Get the baked model data for this model based on the provided string location
	 * @param location The resource path of the baked model (usually the animatable's id string)
	 * @return The BakedGeoModel
	 */
	CoreBakedGeoModel getBakedGeoModel(String location);

	/**
	 * Gets a bone from this model by name.<br>
	 * Generally not a very efficient method, should be avoided where possible.
	 * @param name The name of the bone
	 * @return An {@link Optional} containing the {@link CoreGeoBone} if one matches, otherwise an empty Optional
	 */
	default Optional<? extends CoreGeoBone> getBone(String name) {
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
	 * It is an internal method for automated animation parsing. Use {@link CoreGeoModel#setCustomAnimations(GeoAnimatable, long, AnimationState)} for custom animation work
	 */
	void handleAnimations(E animatable, long instanceId, AnimationState<E> animationState);

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered.<br>
	 * Override to set custom animations (such as head rotation, etc).
	 * @param animatable The {@code GeoAnimatable} instance currently being rendered
	 * @param instanceId The instance id of the {@code GeoAnimatable}
	 * @param animationState An {@link AnimationState} instance created to hold animation data for the {@code animatable} for this method call
	 */
	default void setCustomAnimations(E animatable, long instanceId, AnimationState<E> animationState) {}

	/**
	 * This method is called once per render frame for each {@link GeoAnimatable} being rendered.<br>
	 * Is generally overridden by default to apply the builtin queries, but can be extended further for custom query handling.
	 * @param animatable The {@code GeoAnimatable} instance currently being rendered
	 * @param animTime The internal tick counter kept by the {@link software.bernie.geckolib.core.animation.AnimatableManager manager} for this animatable
	 */
	default void applyMolangQueries(E animatable, double animTime) {}
}
