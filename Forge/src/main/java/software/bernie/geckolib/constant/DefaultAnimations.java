package software.bernie.geckolib.constant;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Optionally usable class that holds constants for recommended animation paths.<br>
 * Using these won't affect much, but it may help keep some consistency in animation namings.<br>
 * Additionally, it encourages use of cached {@link software.bernie.geckolib.core.animation.RawAnimation RawAnimations}, to reduce overheads.
 */
public final class DefaultAnimations {
	public static final RawAnimation ITEM_ON_USE = RawAnimation.begin().thenPlay("item.use");

	public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
	public static final RawAnimation LIVING = RawAnimation.begin().thenLoop("misc.living");
	public static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("misc.spawn");
	public static final RawAnimation DIE = RawAnimation.begin().thenPlay("misc.die");
	public static final RawAnimation INTERACT = RawAnimation.begin().thenPlay("misc.interact");
	public static final RawAnimation DEPLOY = RawAnimation.begin().thenPlay("misc.deploy");
	public static final RawAnimation REST = RawAnimation.begin().thenPlay("misc.rest");
	public static final RawAnimation SIT = RawAnimation.begin().thenPlayAndHold("misc.sit");

	public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
	public static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
	public static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
	public static final RawAnimation DRIVE = RawAnimation.begin().thenLoop("move.drive");
	public static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
	public static final RawAnimation CRAWL = RawAnimation.begin().thenLoop("move.crawl");
	public static final RawAnimation JUMP = RawAnimation.begin().thenPlay("move.jump");
	public static final RawAnimation SNEAK = RawAnimation.begin().thenLoop("move.sneak");

	public static final RawAnimation ATTACK_CAST = RawAnimation.begin().thenPlay("attack.cast");
	public static final RawAnimation ATTACK_SWING = RawAnimation.begin().thenPlay("attack.swing");
	public static final RawAnimation ATTACK_THROW = RawAnimation.begin().thenPlay("attack.throw");
	public static final RawAnimation ATTACK_BITE = RawAnimation.begin().thenPlay("attack.bite");
	public static final RawAnimation ATTACK_SLAM = RawAnimation.begin().thenPlay("attack.slam");
	public static final RawAnimation ATTACK_STOMP = RawAnimation.begin().thenPlay("attack.stomp");
	public static final RawAnimation ATTACK_STRIKE = RawAnimation.begin().thenPlay("attack.strike");
	public static final RawAnimation ATTACK_FLYING_ATTACK = RawAnimation.begin().thenPlay("attack.flying_attack");
	public static final RawAnimation ATTACK_SHOOT = RawAnimation.begin().thenPlay("attack.shoot");
	public static final RawAnimation ATTACK_BLOCK = RawAnimation.begin().thenPlay("attack.block");
	public static final RawAnimation ATTACK_CHARGE = RawAnimation.begin().thenPlay("attack.charge");
	public static final RawAnimation ATTACK_CHARGE_END = RawAnimation.begin().thenPlay("attack.charge_end");
	public static final RawAnimation ATTACK_POWERUP = RawAnimation.begin().thenPlay("attack.powerup");

	/**
	 * A basic predicate-based {@link AnimationController} implementation.<br>
	 * Provide an {@code option A} {@link RawAnimation animation} and an {@code option B} animation, and use the predicate to determine which to play.<br>
	 * Outcome table:
	 * <pre>  true  -> Animation Option A
	 * false -> Animation Option B
	 * null  -> Stop Controller</pre>
	 */
	public static <T extends GeoAnimatable> AnimationController<T> basicPredicateController(T animatable, RawAnimation optionA, RawAnimation optionB, BiFunction<T, AnimationState<T>, Boolean> predicate) {
		return new AnimationController<T>(animatable, "Generic", 10, state -> {
			Boolean result = predicate.apply(animatable, state);

			if (result == null)
				return PlayState.STOP;

			return state.setAndContinue(result ? optionA : optionB);
		});
	}

	/**
	 * Generic {@link DefaultAnimations#LIVING living} controller.<br>
	 * Continuously plays the living animation
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericLivingController(T animatable) {
		return new AnimationController<>(animatable, "Living", 10, state -> state.setAndContinue(LIVING));
	}

	/**
	 * Generic {@link DefaultAnimations#DIE die} controller
	 * <p>
	 * Plays the death animation when dying
	 */
	public static <T extends LivingEntity & GeoAnimatable> AnimationController<T> genericDeathController(T animatable) {
		return new AnimationController<>(animatable, "Death", 0, state -> state.getAnimatable().isDeadOrDying() ? state.setAndContinue(DIE) : PlayState.STOP);
	}

	/**
	 * Generic {@link DefaultAnimations#IDLE idle} controller.<br>
	 * Continuously plays the idle animation
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericIdleController(T animatable) {
		return new AnimationController<T>(animatable, "Idle", 10, state -> state.setAndContinue(IDLE));
	}

	/**
	 * Generic {@link DefaultAnimations#SPAWN spawn} controller.<br>
	 * Plays the spawn animation as long as the current {@link GeoAnimatable#getTick tick} of the animatable is {@literal <=} the value provided in {@code ticks}.<br>
	 * For the {@code objectSupplier}, provide the relevant object for the animatable being animated.
	 * Recommended:
	 * <ul>
	 *     <li>{@link software.bernie.geckolib.animatable.GeoEntity GeoEntity}: state -> animatable</li>
	 *     <li>{@link software.bernie.geckolib.animatable.GeoBlockEntity GeoBlockEntity}: state -> animatable</li>
	 *     <li>{@link software.bernie.geckolib.animatable.GeoReplacedEntity GeoReplacedEntity}: state -> state.getData(DataTickets.ENTITY)</li>
	 *     <li>{@link software.bernie.geckolib.animatable.GeoItem GeoItem}: state -> state.getData(DataTickets.ITEMSTACK)</li>
	 *     <li>{@code GeoArmor}: state -> state.getData(DataTickets.ENTITY)</li>
	 * </ul>
	 * @param animatable The animatable the animation is for
	 * @param objectSupplier The supplier of the associated object for the {@link GeoAnimatable#getTick} call
	 * @param ticks The number of ticks the animation should run for. After this value is surpassed, the animation will no longer play
	 */
	public static <T extends GeoAnimatable> AnimationController<T> getSpawnController(T animatable, Function<AnimationState<T>, Object> objectSupplier, int ticks) {
		return new AnimationController<T>(animatable, "Spawn", 0, state -> {
			if (animatable.getTick(objectSupplier.apply(state)) <= ticks)
				return state.setAndContinue(DefaultAnimations.SPAWN);

			return PlayState.STOP;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} controller.<br>
	 * Will play the walk animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericWalkController(T animatable) {
		return new AnimationController<T>(animatable, "Walk", 0, state -> {
			if (state.isMoving())
				return state.setAndContinue(WALK);

			return PlayState.STOP;
		});
	}

	/**
	 * Generic attack controller.<br>
	 * Plays an attack animation if the animatable is {@link net.minecraft.world.entity.LivingEntity#swinging}.<br>
	 * Resets the animation each time it stops, ready for the next swing
	 * @param animatable The entity that should swing
	 * @param attackAnimation The attack animation to play (E.G. swipe, strike, stomp, swing, etc)
	 * @return A new {@link AnimationController} instance to use
	 */
	public static <T extends LivingEntity & GeoAnimatable> AnimationController<T> genericAttackAnimation(T animatable, RawAnimation attackAnimation) {
		return new AnimationController<>(animatable, "Attack", 5, state -> {
			if (animatable.swinging)
				return state.setAndContinue(attackAnimation);

			state.getController().forceAnimationReset();

			return PlayState.STOP;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} + {@link DefaultAnimations#IDLE idle} controller.<br>
	 * Will play the walk animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericWalkIdleController(T animatable) {
		return new AnimationController<T>(animatable, "Walk/Idle", 0, state -> state.setAndContinue(state.isMoving() ? WALK : IDLE));
	}

	/**
	 * Generic {@link DefaultAnimations#SWIM swim} controller.<br>
	 * Will play the swim animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericSwimController(T entity) {
		return new AnimationController<T>(entity, "Swim", 0, state -> {
			if (state.isMoving())
				return state.setAndContinue(SWIM);

			return PlayState.STOP;
		});
	}

	/**
	 * Generic {@link DefaultAnimations#SWIM swim} + {@link DefaultAnimations#IDLE idle} controller.<br>
	 * Will play the swim animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericSwimIdleController(T animatable) {
		return new AnimationController<T>(animatable, "Swim/Idle", 0, state -> state.setAndContinue(state.isMoving() ? SWIM : IDLE));
	}

	/**
	 * Generic {@link DefaultAnimations#FLY walk} controller.<br>
	 * Will play the fly animation if the animatable is considered moving, or stop if not.
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericFlyController(T animatable) {
		return new AnimationController<T>(animatable, "Fly", 0, state -> state.setAndContinue(FLY));
	}

	/**
	 * Generic {@link DefaultAnimations#FLY walk} + {@link DefaultAnimations#IDLE idle} controller.<br>
	 * Will play the walk animation if the animatable is considered moving, or idle if not
	 */
	public static <T extends GeoAnimatable> AnimationController<T> genericFlyIdleController(T animatable) {
		return new AnimationController<T>(animatable, "Fly/Idle", 0, state -> state.setAndContinue(state.isMoving() ? FLY : IDLE));
	}

	/**
	 * Generic {@link DefaultAnimations#WALK walk} + {@link DefaultAnimations#RUN run} + {@link DefaultAnimations#IDLE idle} controller.<br>
	 * If the entity is considered moving, will either walk or run depending on the {@link Entity#isSprinting()} method, otherwise it will idle
	 */
	public static <T extends Entity & GeoAnimatable> AnimationController<T> genericWalkRunIdleController(T entity) {
		return new AnimationController<T>(entity, "Walk/Run/Idle", 0, state -> {
			if (state.isMoving()) {
				return state.setAndContinue(entity.isSprinting() ? RUN : WALK);
			}
			else {
				return state.setAndContinue(IDLE);
			}
		});
	}
}
